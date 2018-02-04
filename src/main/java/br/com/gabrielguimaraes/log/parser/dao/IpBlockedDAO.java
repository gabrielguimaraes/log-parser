package br.com.gabrielguimaraes.log.parser.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.gabrielguimaraes.log.parser.arguments.ArgumentsContext;
import br.com.gabrielguimaraes.log.parser.model.IpBlocked;
import br.com.gabrielguimaraes.log.parser.model.IpCount;

public class IpBlockedDAO extends BasicDAO<IpBlocked>{
    
    public List<Integer> saveIpBlockedFromIpCountArguments(List<IpCount> ipCountList, ArgumentsContext argumentsContext) {
        List<IpBlocked> ipBlockedList = ipCountList
            .stream()
            .map(ipCount -> new IpBlocked(ipCount.getIp(), argumentsContext.createReason("Total of requisitons: " + ipCount.getCount().toString())))
            .collect(Collectors.toList());
        
        return save(ipBlockedList);
    }
    
    public List<Integer> save(List<IpBlocked> ipBlockedList) {
        if (ipBlockedList == null || ipBlockedList.isEmpty()) {
            return Arrays.asList(0);
        }
        String sqlFields = EntityMappingHelper.getSingleEntityValuesTerms(IpBlocked.class, ipBlockedList.get(0));
        
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO ip_blocked ")
                .append(sqlFields);
        
        List<List<Map<Class<?>, Object>>> logDataMappedList = 
        ipBlockedList
            .stream()
            .map(ipBlocked -> parseToListOfMappedValues(ipBlocked))
            .collect(Collectors.toList());
        
        return this.database.saveBatch(queryBuilder.toString(), logDataMappedList);
    }
    
	@Override
    public IpBlocked fillData(ResultSet resultSet) {
        IpBlocked ipBlocked = new IpBlocked();
        try {
            ipBlocked.setId(resultSet.getInt("id"));
            ipBlocked.setIp(resultSet.getString("ip"));
            ipBlocked.setReason(resultSet.getString("reason"));
            
            return ipBlocked;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot get resultSet values to IpBlocked object\n", e);
        }
        
    }
}
