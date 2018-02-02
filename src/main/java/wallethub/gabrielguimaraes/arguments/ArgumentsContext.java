package wallethub.gabrielguimaraes.arguments;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import wallethub.gabrielguimaraes.helper.FieldConversionHelper;

public class ArgumentsContext {

    private final static Map<String, Class<?>> PARAMETERS = new HashMap<>();
    private final static Map<String, String> PARAMETER_NAMES = new HashMap<>();
    {
        PARAMETERS.put("--startDate=", LocalDateTime.class);
        PARAMETERS.put("--duration=", LogDuration.class);
        PARAMETERS.put("--threshold=", Integer.class);

        PARAMETER_NAMES.put("--startDate=", "startDate");
        PARAMETER_NAMES.put("--duration=", "duration");
        PARAMETER_NAMES.put("--threshold=", "thresold");

    }
    private LocalDateTime startDate;
    private LogDuration duration;
    private Integer threshold;

    public ArgumentsContext(String... args) {
        this.parseArgumentsToContext(args);
    }

    public void parseArgumentsToContext(String... args) {
        Stream.of(args).forEach(this::findParameterNames);
    }

    public void findParameterNames(String parameter) {
        PARAMETERS.forEach((k, v) -> fillParameterValue(parameter, k, v));
    }

    private void fillParameterValue(String parameter, String argumentName, Class<?> clazz) {
        int index = parameter.indexOf(argumentName);

        if (index == -1) {
            return;
        }

        String parameterName = PARAMETER_NAMES.get(argumentName);
        String parameterValue = parameter.substring(index, parameter.length() - 1);

        try {
            Field field = this.getClass().getDeclaredField(parameterName);
            field.set(this, clazz.cast(getObjectAccordingToFieldType(parameterValue, clazz)));
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException e) {
            System.out.format("Cannot call context field ", e);
        } catch (NoSuchFieldException e) {
            System.out.format("Cannot find context field", e);
        }

    }

    private Object getObjectAccordingToFieldType(String parameterValue, Class<?> clazz) {
        if (clazz.equals(LocalDateTime.class)) {
            return FieldConversionHelper.parseLocalDateTime(parameterValue);
        }

        if (clazz.equals(Integer.class)) {
            return Integer.parseInt(parameterValue);
        }

        if (clazz.equals(LogDuration.class)) {
            return LogDuration.fromAlias(parameterValue);
        }

        return null;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LogDuration getDuration() {
        return duration;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setDuration(LogDuration duration) {
        this.duration = duration;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "ArgumentsContext [startDate=" + startDate + ", duration=" + duration + ", threshold=" + threshold + "]";
    }
}
