package co.com.bancolombia.model.log.gateways;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface LoggerService {
    void info(String message, Object... args);
    void warn(String message, Object... args);
    void error(String message, Object... args);
    void error(String message, JsonProcessingException id, String eMessage, String throwable);
}