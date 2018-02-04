package br.com.gabrielguimaraes.log.parser.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface BasicDAO<T> {
    T fillData(ResultSet resultSet);

    List<T> getObjectsFromResultSet(ResultSet resultSet);

    List<Map<Class<?>, Object>> parseToListOfMappedValues(T object);
}
