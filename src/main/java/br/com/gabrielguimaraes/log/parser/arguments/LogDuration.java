package br.com.gabrielguimaraes.log.parser.arguments;

import java.util.stream.Stream;

public enum LogDuration {
    HOURLY("hourly"),
    DAILY("daily");
    
    private String alias;
    
    private LogDuration(String alias) {
        this.alias = alias;
    }
    
    public String getAlias() {
        return this.alias;
    }
    
    public static LogDuration fromAlias(String alias) {
        if (alias == null) {
            return null;
        }
        return Stream.of(values())
            .filter(ld -> alias.equals(ld.getAlias()))
            .findFirst()
            .orElse(null);
    }
}
