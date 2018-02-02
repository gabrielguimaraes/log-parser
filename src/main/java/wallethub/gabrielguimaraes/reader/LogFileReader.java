package wallethub.gabrielguimaraes.reader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import wallethub.gabrielguimaraes.helper.FieldConversionHelper;
import wallethub.gabrielguimaraes.model.LogData;

public class LogFileReader {
    public String readLogFile(String logFileName) {
        StringBuilder result = new StringBuilder();

        Stream<String> fileLines = readLogFileLines(logFileName);
        if (fileLines == null) {
            return null;
        }
        fileLines.forEach(fileLine -> result.append(fileLine).append("/"));
        fileLines.close();

        return result.toString();
    }

    public Stream<String> readLogFileLines(String logFileName) {
        Stream<String> fileLines = null;
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource(logFileName).toURI());
            fileLines = Files.lines(path);
        } catch (URISyntaxException e) {
            System.out.printf("Cannot read path to load log file name. %s", e);
        } catch (IOException e) {
            System.out.printf("Cannot find file when trying to load log file. %s", e);
        }
        return fileLines;
    }
    
    
    public LogData parseFromLogLine(String logLine) {
        String[] logAttributes = logLine.split("\\|");
        LogData logData = new LogData();
        
        logData.setExecutionDate(FieldConversionHelper.parseLocalDateTime(logAttributes[0]));
        logData.setIp(logAttributes[1].replaceAll("\"", ""));
        logData.setRequest(logAttributes[2].replaceAll("\"", ""));
        logData.setStatus(Integer.parseInt(logAttributes[3]));
        logData.setUserAgent(logAttributes[4].replaceAll("\"", ""));
        
        return logData;
    }

}
