package br.com.gabrielguimaraes.log.parser.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import br.com.gabrielguimaraes.log.parser.helper.FieldConversionHelper;
import br.com.gabrielguimaraes.log.parser.model.LogData;

public class LogFileReader {
    public static String readLogFile(String logFileName) {
        StringBuilder result = new StringBuilder();

        Stream<String> fileLines = readLogFileLines(logFileName);
        if (fileLines == null) {
            return null;
        }
        fileLines.forEach(fileLine -> result.append(fileLine).append("/"));
        fileLines.close();

        return result.toString();
    }

    public static Stream<String> readLogFileLines(String logFileName) {
        if (logFileName == null || logFileName.isEmpty()) {
            return Stream.empty();
        }
        
        Stream<String> fileLines = Stream.empty();;
        try {
//            Path path = Paths.get(getClass().getClassLoader().getResource(logFileName).toURI());
            Path path = Paths.get(logFileName).toAbsolutePath();
            fileLines = Files.lines(path);
//        } catch (URISyntaxException e) {
//            System.out.printf("Cannot read path to load log file name. %s", e);
        } catch (IOException e) {
            System.out.printf("Cannot find file when trying to load log file. %s\n", e);
        }
        return fileLines;
    }
    
    
    public static LogData parseFromLogLine(String logLine) {
        String[] logAttributes = logLine.split("\\|");
        LogData logData = new LogData();
        
        logData.setExecutionDate(FieldConversionHelper.parseLogLocalDateTime(logAttributes[0]));
        logData.setIp(logAttributes[1].replaceAll("\"", ""));
        logData.setRequest(logAttributes[2].replaceAll("\"", ""));
        logData.setStatus(Integer.parseInt(logAttributes[3]));
        logData.setUserAgent(logAttributes[4].replaceAll("\"", ""));
        
        return logData;
    }

}
