package wallethub.gabrielguimaraes.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import wallethub.gabrielguimaraes.database.MySQLDatabaseAccess;
import wallethub.gabrielguimaraes.model.LogData;

public class LogDataDAO implements BasicDAO<LogData>{
	
    private MySQLDatabaseAccess database;
    
    public LogDataDAO() {
        this.database = new MySQLDatabaseAccess();
    }
    
	public List<LogData> findAll() {
		ResultSet resultSet = database.selectQuery("SELECT * FROM log_data", null);
		
		return getObjectsFromResultSet(resultSet);
	}
	
	
	public Integer save(LogData logData) {
	    String sqlFields = EntityMappingHelper.getSingleEntityValuesTerms(LogData.class, logData);
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO log_data ")
	        .append(sqlFields);
        
        int result = database.save(queryBuilder.toString(), parseToListOfMappedValues(logData));
	    
        return result;
	}
	
	public List<Integer> save(List<LogData> logDataList) {
	    if (logDataList == null || logDataList.isEmpty()) {
	        return Arrays.asList(0);
	    }
	    String sqlFields = EntityMappingHelper.getSingleEntityValuesTerms(LogData.class, logDataList.get(0));
	    
	    StringBuilder queryBuilder = new StringBuilder("INSERT INTO log_data ")
	            .append(sqlFields);
	    
	    List<List<Map<Class<?>, Object>>> logDataMappedList = 
        logDataList
	        .stream()
	        .map(logData -> parseToListOfMappedValues(logData))
	        .collect(Collectors.toList());
	    
	    return this.database.saveBatch(queryBuilder.toString(), logDataMappedList);
    }
	

	
	@Override
	public List<Map<Class<?>, Object>> parseToListOfMappedValues(LogData logData) {
	    List<Map<Class<?>, Object>> listObject = Stream.of(LogData.class.getDeclaredFields())
	        .map(f -> EntityMappingHelper.addObjectToMap(f, logData))
	        .filter(m -> m != null && !m.isEmpty())
            .collect(Collectors.toList());

	    return listObject;
	}
	
	
	@Override
    public List<LogData> getObjectsFromResultSet(ResultSet resultSet) {
        List<LogData> logDataList = new ArrayList<>();
        try {
            while(resultSet.next()) {
                logDataList.add(fillData(resultSet));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot get next ResultSet", e);
        } finally {
            this.database.close();
        }
        
        
        return logDataList;
    }

	@Override
    public LogData fillData(ResultSet resultSet) {
        LogData logData = new LogData();
        try {
            logData.setId(resultSet.getInt("id"));
            logData.setExecutionDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(resultSet.getTimestamp("execution_date").getTime()), TimeZone.getDefault().toZoneId()));
            logData.setIp(resultSet.getString("ip"));;
            logData.setRequest(resultSet.getString("request"));
            logData.setStatus(resultSet.getInt("status"));
            logData.setUserAgent(resultSet.getString("user_agent"));
            
            return logData;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot get resultSet values to LogData object", e);
        }
        
    }
}
