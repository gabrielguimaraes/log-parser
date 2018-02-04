package br.com.gabrielguimaraes.log.parser.arguments;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import br.com.gabrielguimaraes.log.parser.helper.FieldConversionHelper;

public class ArgumentsParser {

    private final static Map<String, Class<?>> PARAMETERS = new HashMap<>();
    private final static Map<String, String> PARAMETER_NAMES = new HashMap<>();
    private final static String ACCESS_LOG = "accesslog";
    
    {
        PARAMETERS.put("--accesslog=", String.class);
        PARAMETERS.put("--startDate=", LocalDateTime.class);
        PARAMETERS.put("--duration=", LogDuration.class);
        PARAMETERS.put("--threshold=", Integer.class);

        PARAMETER_NAMES.put("--accesslog=", ACCESS_LOG);
        PARAMETER_NAMES.put("--startDate=", "startDate");
        PARAMETER_NAMES.put("--duration=", "duration");
        PARAMETER_NAMES.put("--threshold=", "threshold");
    }
    
    private String accesslog;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LogDuration duration;
    private Integer threshold;

    
    public ArgumentsParser(String... args) {
        this.parseArgumentsToContext(args);
    }

    public void parseArgumentsToContext(String... args) {
        Stream.of(args).forEach(this::findParameterNames);
    }

    public void findParameterNames(String parameter) {
        PARAMETERS.forEach((k, v) -> fillParameterValue(parameter, k, v));
    }

    private void fillParameterValue(String parameter, String argumentName, Class<?> clazz) {
        int index = parameter.lastIndexOf(argumentName);

        if (index != 0) {
            return;
        }

        String parameterName = PARAMETER_NAMES.get(argumentName);
        String parameterValue = parameter.substring(argumentName.length(), parameter.length());

        setForObject(parameterName, parameterValue, clazz, this);
        return;
//        System.out.format("Found parameter %s with value %s\n", parameterName, parameterValue);
    }
    
    private void setForObject(String parameterName, String parameterValue, Class<?> clazz, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(parameterName);
            field.set(object, clazz.cast(getObjectAccordingToFieldType(parameterValue, clazz)));
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException e) {
            System.out.format("Cannot call context field for argument %s.\n", parameterName);
            e.getStackTrace();
        } catch (NoSuchFieldException e) {
            System.out.format("Cannot find context field for argument %s.\n", parameterName);
            e.getStackTrace();
        }
    }

    private Object getObjectAccordingToFieldType(String parameterValue, Class<?> clazz) {
        if (clazz.equals(LocalDateTime.class)) {
            try {
                LocalDateTime startDate = FieldConversionHelper.parseArgumentLocalDateTime(parameterValue);
                return startDate;
            } catch (DateTimeParseException e) {
                System.out.format("The datetime value [%s] is not in the correct format. yyyy-MM-dd.HH:mm:ss\n", parameterValue);
                return null;
            }
        }

        if (clazz.equals(Integer.class)) {
            return Integer.parseInt(parameterValue);
        }

        if (clazz.equals(LogDuration.class)) {
            return LogDuration.fromAlias(parameterValue);
        }
        
        return parameterValue;
    }

    public String getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LogDuration getDuration() {
        return duration;
    }

    public void setDuration(LogDuration duration) {
        this.duration = duration;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}
