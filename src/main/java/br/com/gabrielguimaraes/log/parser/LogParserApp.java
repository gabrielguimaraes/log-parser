package br.com.gabrielguimaraes.log.parser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.gabrielguimaraes.log.parser.arguments.ArgumentsContext;
import br.com.gabrielguimaraes.log.parser.arguments.ArgumentsParser;
import br.com.gabrielguimaraes.log.parser.dao.IpBlockedDAO;
import br.com.gabrielguimaraes.log.parser.dao.IpCountDAO;
import br.com.gabrielguimaraes.log.parser.dao.LogDataDAO;
import br.com.gabrielguimaraes.log.parser.model.IpCount;
import br.com.gabrielguimaraes.log.parser.model.LogData;
import br.com.gabrielguimaraes.log.parser.reader.LogFileReader;

public class LogParserApp {
    public static void main(String[] args) {
        ArgumentsParser argumentsParser = new ArgumentsParser(args);

        Stream<String> data = LogFileReader.readLogFileLines(argumentsParser.getAccesslog());

        ArgumentsContext argumentsContext = ArgumentsContext.fromArgumentsParser(argumentsParser);

        readFromFile(data);
        
        List<IpCount> ipCountList = findBlockedIpsByArguments(argumentsContext);
        
        IpBlockedDAO ipBlockedDAO = new IpBlockedDAO();
        ipBlockedDAO.saveIpBlockedFromIpCountArguments(ipCountList, argumentsContext);
    }

    private static List<IpCount> findBlockedIpsByArguments(ArgumentsContext argumentsContext) {
        IpCountDAO ipCountDAO = new IpCountDAO();
        List<IpCount> ipCountList = ipCountDAO.findByStartDateAndDurationAndCountIpGreaterThanThreshold(argumentsContext);
        ipCountList.forEach(System.out::println);
        return ipCountList;
    }

    private static void readFromFile(Stream<String> data) {
        List<LogData> logDataList = data.map(LogFileReader::parseFromLogLine).collect(Collectors.toList());
        
        LogDataDAO logDataDAO = new LogDataDAO();
        logDataDAO.save(logDataList);
    }
}
