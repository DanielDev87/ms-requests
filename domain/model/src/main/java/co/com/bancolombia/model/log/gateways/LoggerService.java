package co.com.bancolombia.model.log.gateways;

public interface LoggerService {
    void info(String message, Object... args);
    void warn(String message, Object... args);
    void error(String message, Throwable throwable);
}