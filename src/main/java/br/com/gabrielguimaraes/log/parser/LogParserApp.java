package br.com.gabrielguimaraes.log.parser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.gabrielguimaraes.log.parser.arguments.ArgumentsContext;
import br.com.gabrielguimaraes.log.parser.arguments.ArgumentsParser;
import br.com.gabrielguimaraes.log.parser.dao.IpCountDAO;
import br.com.gabrielguimaraes.log.parser.dao.LogDataDAO;
import br.com.gabrielguimaraes.log.parser.model.IpCount;
import br.com.gabrielguimaraes.log.parser.model.LogData;
import br.com.gabrielguimaraes.log.parser.reader.LogFileReader;

public class LogParserApp {
    public static void main(String[] args) {

//        Stream.of(args)
//            .forEach(System.out::println);
        
        ArgumentsParser argumentsParser = new ArgumentsParser(args);

        Stream<String> data = LogFileReader.readLogFileLines(argumentsParser.getAccesslog());

        ArgumentsContext argumentsContext = ArgumentsContext.fromArgumentsParser(argumentsParser);

        List<LogData> logDataList = data.map(LogFileReader::parseFromLogLine).collect(Collectors.toList());
        
        LogDataDAO logDataDAO = new LogDataDAO();
        logDataDAO.save(logDataList);
        
        IpCountDAO ipCountDAO = new IpCountDAO();
        
        List<IpCount> ipCountList = ipCountDAO.findByStartDateAndDurationAndCountIpGreaterThanThreshold(argumentsContext);
        
//        List<LogData> findAll = logDataDAO.findAll();
        ipCountList.forEach(System.out::println);
    }
}
