package br.com.gabrielguimaraes.log.parser.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import br.com.gabrielguimaraes.log.parser.arguments.ArgumentsContext;
import br.com.gabrielguimaraes.log.parser.model.IpCount;

public class IpCountDAO extends BasicDAO<IpCount>{
	
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
