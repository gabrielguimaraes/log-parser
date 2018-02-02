package wallethub.gabrielguimaraes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import wallethub.gabrielguimaraes.arguments.ArgumentsContext;
import wallethub.gabrielguimaraes.dao.LogDataDAO;
import wallethub.gabrielguimaraes.model.LogData;
import wallethub.gabrielguimaraes.reader.LogFileReader;

public class LogParserApp {
    public static void main(String[] args) {

        ArgumentsContext argumentsContext = new ArgumentsContext(args);

        LogDataDAO logDataDAO = new LogDataDAO();

        LogFileReader logFileReader = new LogFileReader();

        Stream<String> data = logFileReader.readLogFileLines("access.log");

        List<LogData> logDataList = data.map(logFileReader::parseFromLogLine).collect(Collectors.toList());

        // logDataDAO.save(logDataList);
        List<LogData> findAll = logDataDAO.findAll();

        findAll.forEach(System.out::println);
    }
}
