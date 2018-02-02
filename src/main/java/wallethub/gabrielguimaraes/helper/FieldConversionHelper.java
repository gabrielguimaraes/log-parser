package wallethub.gabrielguimaraes.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FieldConversionHelper {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static LocalDateTime parseLocalDateTime(String localDateTimeString) {
        return LocalDateTime.parse(localDateTimeString, FieldConversionHelper.FORMATTER);
    }
}
