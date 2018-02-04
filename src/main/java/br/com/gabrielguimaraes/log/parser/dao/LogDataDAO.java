package br.com.gabrielguimaraes.log.parser.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import br.com.gabrielguimaraes.log.parser.model.LogData;

public class LogDataDAO extends BasicDAO<LogData>{
	
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
