package br.com.gabrielguimaraes.log.parser.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import br.com.gabrielguimaraes.log.parser.arguments.LogDuration;

public class FieldConversionHelper {
    private static final String DATE_FORMAT_ARGUMENT = "yyyy-MM-dd.HH:mm:ss";
    private static final DateTimeFormatter FORMATTER_ARGUMENT = DateTimeFormatter.ofPattern(DATE_FORMAT_ARGUMENT);
    private static final String DATE_FORMAT_LOG = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final DateTimeFormatter FORMATTER_LOG = DateTimeFormatter.ofPattern(DATE_FORMAT_LOG);

    public static LocalDateTime parseArgumentLocalDateTime(String localDateTimeString) throws DateTimeParseException {
        return LocalDateTime.parse(localDateTimeString, FieldConversionHelper.FORMATTER_ARGUMENT);
    }

    public static LocalDateTime parseLogLocalDateTime(String localDateTimeString) throws DateTimeParseException {
        return LocalDateTime.parse(localDateTimeString, FieldConversionHelper.FORMATTER_LOG);
    }
    
    public static LocalDateTime addTimeAccordingToDurationArgument(LocalDateTime startDate, LogDuration logDuration) {
        if (logDuration == null) {
            return null;
        }
        
        LocalDateTime endDate = null;
        if (logDuration == LogDuration.HOURLY) {
            endDate = startDate.plusHours(1);
        }
        
        if (logDuration == LogDuration.DAILY) {
            endDate = startDate.plusDays(1);
        }
        
        return endDate;
    }
}
