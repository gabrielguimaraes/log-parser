package br.com.gabrielguimaraes.log.parser.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.gabrielguimaraes.log.parser.database.MySQLDatabaseAccess;

public abstract class BasicDAO<T> {
    
    protected MySQLDatabaseAccess database;
    
    public BasicDAO() {
        this.database = new MySQLDatabaseAccess();
    }
    
    abstract T fillData(ResultSet resultSet);

    List<T> getObjectsFromResultSet(ResultSet resultSet) {
        List<T> objectList = new ArrayList<>();
        try {
            while(resultSet.next()) {
                objectList.add(fillData(resultSet));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot get next ResultSet", e);
        } finally {
            this.database.close();
        }
        
        
        return objectList;
    }

    List<Map<Class<?>, Object>> parseToListOfMappedValues(T object) {
        List<Map<Class<?>, Object>> listObject = Stream.of(object.getClass().getDeclaredFields())
                .map(f -> EntityMappingHelper.addObjectToMap(f, object))
                .filter(m -> m != null && !m.isEmpty())
                .collect(Collectors.toList());

            return listObject;
    }
}
