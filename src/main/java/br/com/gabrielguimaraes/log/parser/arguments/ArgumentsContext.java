package br.com.gabrielguimaraes.log.parser.arguments;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.gabrielguimaraes.log.parser.dao.EntityMappingHelper;
import br.com.gabrielguimaraes.log.parser.helper.FieldConversionHelper;

public class ArgumentsContext {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer threshold;

    private ArgumentsContext() {}
    
    public static ArgumentsContext fromArgumentsParser(ArgumentsParser argumentsParser) {
        ArgumentsContext argumentsContext = new ArgumentsContext();
        argumentsContext.setStartDate(argumentsParser.getStartDate());
        argumentsContext.setEndDate(calculateEndDate(argumentsParser));
        argumentsContext.setThreshold(argumentsParser.getThreshold());
        
        return argumentsContext;
    }
    
    private static LocalDateTime calculateEndDate(ArgumentsParser argumentsParser) {
        if (argumentsParser.getStartDate() == null || argumentsParser.getDuration() == null) {
            return null;
        }
        
        return FieldConversionHelper.addTimeAccordingToDurationArgument(argumentsParser.getStartDate(), argumentsParser.getDuration());
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

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public List<Map<Class<?>, Object>> parseToListOfMappedValues() {
        List<Map<Class<?>, Object>> listObject = Stream.of(ArgumentsContext.class.getDeclaredFields())
            .map(f -> EntityMappingHelper.addObjectToMap(f, this))
            .filter(m -> m != null && !m.isEmpty())
            .collect(Collectors.toList());

        return listObject;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((threshold == null) ? 0 : threshold.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ArgumentsContext other = (ArgumentsContext) obj;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (threshold == null) {
            if (other.threshold != null)
                return false;
        } else if (!threshold.equals(other.threshold))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ArgumentsContext [startDate=" + startDate + ", endDate=" + endDate + ", threshold=" + threshold + "]";
    }
}
