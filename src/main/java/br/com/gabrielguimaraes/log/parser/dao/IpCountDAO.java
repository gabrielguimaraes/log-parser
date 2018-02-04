package br.com.gabrielguimaraes.log.parser.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.gabrielguimaraes.log.parser.arguments.ArgumentsContext;
import br.com.gabrielguimaraes.log.parser.database.MySQLDatabaseAccess;
import br.com.gabrielguimaraes.log.parser.model.IpCount;

public class IpCountDAO implements BasicDAO<IpCount>{
	
    private MySQLDatabaseAccess database;
    
    public IpCountDAO() {
        this.database = new MySQLDatabaseAccess();
    }
    
    public List<IpCount> findByStartDateAndDurationAndCountIpGreaterThanThreshold(ArgumentsContext argumentsContext) {
        ResultSet resultSet = database.selectQuery(
        " SELECT ip as ip, count(ip) as count FROM log_data " +
        " WHERE execution_date BETWEEN ?  " +
        " AND ?  " +
        " GROUP BY ip  " +
        " HAVING count(ip) >= ?  ", argumentsContext.parseToListOfMappedValues());
        
        return getObjectsFromResultSet(resultSet);
    }
    
	@Override
	public List<Map<Class<?>, Object>> parseToListOfMappedValues(IpCount ipCount) {
	    List<Map<Class<?>, Object>> listObject = Stream.of(ipCount.getClass().getDeclaredFields())
	        .map(f -> EntityMappingHelper.addObjectToMap(f, ipCount))
	        .filter(m -> m != null && !m.isEmpty())
            .collect(Collectors.toList());

	    return listObject;
	}
	
	@Override
    public List<IpCount> getObjectsFromResultSet(ResultSet resultSet) {
        List<IpCount> ipCountList = new ArrayList<>();
        try {
            while(resultSet.next()) {
                ipCountList.add(fillData(resultSet));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot get next ResultSet", e);
        } finally {
            this.database.close();
        }
        
        
        return ipCountList;
    }

	@Override
    public IpCount fillData(ResultSet resultSet) {
        IpCount ipCount = new IpCount();
        try {
            ipCount.setIp(resultSet.getString("ip"));
            ipCount.setCount(resultSet.getInt("count"));
            
            return ipCount;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot get resultSet values to IpCount object", e);
        }
        
    }
}
