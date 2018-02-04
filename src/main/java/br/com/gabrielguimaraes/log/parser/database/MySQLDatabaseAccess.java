package br.com.gabrielguimaraes.log.parser.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MySQLDatabaseAccess {
    public static final String URL = "jdbc:mysql://localhost:3306/log_reader?useSSL=false";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "mysql";

    private Connection databaseConnection;

    public Connection connectToDatabase() {
//        System.out.println("Connecting to mysql database for Log Reader");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            this.databaseConnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//            System.out.println("Database connected");
            
            return this.databaseConnection;
            
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find mysql jdbc driver", e);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    public void close() {
        try {
            if (this.databaseConnection != null && !this.databaseConnection.isClosed()) {
                this.databaseConnection.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot close connection to database", e);
        }

    }

    private ResultSet validateAndProcessAsSelectStatement(String query, List<Map<Class<?>, Object>> parameters) {

        try {
            if (parameters == null || parameters.isEmpty()) {
                Statement statement = this.databaseConnection.createStatement();
                return statement.executeQuery(query);
            }

            return null;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot execute SQL on database", e);
        }
    }

    private void preparedStatementForEachType(PreparedStatement preparedStatement, int index, Class<?> type,
            Object value) {
        if (index < 1 || type == null) {
            return;
        }

        try {
            if (value == null) {
                preparedStatement.setNull(index, convertJavaTypeToSQLType(type));
                return;
            }

            if (type.equals(String.class)) {
                preparedStatement.setString(index, (String) value);
                return;
            }

            if (type.equals(Integer.class)) {
                preparedStatement.setInt(index, (Integer) value);
                return;
            }

            if (type.equals(LocalDateTime.class)) {
                preparedStatement.setTimestamp(index, Timestamp.valueOf((LocalDateTime) value));
                return;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot map ResultSet to Object correctly", e);
        }

    }

    private int convertJavaTypeToSQLType(Class<?> type) {
        if (type.equals(String.class)) {
            return Types.VARCHAR;
        }

        if (type.equals(Integer.class)) {
            return Types.INTEGER;
        }

        if (type.equals(Long.class)) {
            return Types.BIGINT;
        }

        if (type.equals(BigDecimal.class)) {
            return Types.DECIMAL;
        }

        if (type.equals(LocalDateTime.class)) {
            return Types.TIMESTAMP;
        }

        return Types.NULL;
    }

    public final ResultSet selectQuery(String query, List<Map<Class<?>, Object>> parameters) {
        if (query == null) {
            return null;
        }

        try {
            connectToDatabase();

            ResultSet resultSet = validateAndProcessAsSelectStatement(query, parameters);
            if (resultSet != null) {
                return resultSet;
            }

            PreparedStatement preparedStatement = this.databaseConnection.prepareStatement(query);

            parameters.forEach(m -> m.forEach(
                    (k, v) -> preparedStatementForEachType(preparedStatement, parameters.indexOf(m) + 1, k, v)));

            resultSet = preparedStatement.executeQuery();

            return resultSet;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new IllegalStateException("Cannot execute SQL on database", e);
        }
    }

    public int save(String query, List<Map<Class<?>, Object>> parameters) {
        if (query == null || parameters == null || parameters.isEmpty()) {
            return -1;
        }

        try {
            connectToDatabase();

            PreparedStatement preparedStatement = this.databaseConnection.prepareStatement(query);

            parameters.forEach(m -> m.forEach(
                    (k, v) -> preparedStatementForEachType(preparedStatement, parameters.indexOf(m) + 1, k, v)));

            int result = preparedStatement.executeUpdate();

            return result;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new IllegalStateException("Cannot execute SQL on database", e);
        } finally {
            close();
        }
    }

    public List<Integer> saveBatch(String query, List<List<Map<Class<?>, Object>>> batchParameters) {
        if (query == null || batchParameters == null || batchParameters.isEmpty()) {
            return Arrays.asList(-1);
        }

        try {
            connectToDatabase();

            this.databaseConnection.setAutoCommit(false);
            PreparedStatement preparedStatement = this.databaseConnection.prepareStatement(query);

            int begin = 0;
            int increment = 10000;
            int end = batchParameters.size() - 1;
            
            List<List<List<Map<Class<?>, Object>>>> groupOfLists = IntStream
                .iterate(begin, i -> i + increment)
                .limit((end / increment) + 1)
                .mapToObj(i -> batchParameters.subList(i, isEndOfListIndex(i, end, increment)))
                .collect(Collectors.toList());
            
            List<Integer> result = groupOfLists
                .stream()
                .map(list -> prepareStatementForBatch(preparedStatement, list))
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());
                
            this.databaseConnection.setAutoCommit(true);

            return result;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new IllegalStateException("Cannot execute SQL on database", e);
        } finally {
            close();
        }

    }

    private int isEndOfListIndex(int i, int end, int increment) {
        int endSublist = i + increment;
        if (endSublist > end) {
            endSublist = end + 1;
        }
        return endSublist;
    }

    public List<Integer> prepareStatementForBatch(PreparedStatement preparedStatement, List<List<Map<Class<?>, Object>>> parametersGroup) {
        try {
            parametersGroup.forEach(list -> addBatchStatement(preparedStatement, list));
            int[] executeBatchResult = preparedStatement.executeBatch();
            this.databaseConnection.commit();
//            System.out.println("INSERTED " + parametersGroup.size());
            return IntStream
                .of(executeBatchResult)
                .boxed()
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot execute Batch insert SQL on database", e);
        }
    }

    private void addBatchStatement(PreparedStatement preparedStatement, List<Map<Class<?>, Object>> parameters) {
        try {
            parameters.forEach(m -> m.forEach(
                    (k, v) -> preparedStatementForEachType(preparedStatement, parameters.indexOf(m) + 1, k, v)));
            preparedStatement.addBatch();
        } catch (SQLException e) {
            System.out.printf("Cannot add batch prepared statement to save sql. %s\n", e);
        }
    }
}
