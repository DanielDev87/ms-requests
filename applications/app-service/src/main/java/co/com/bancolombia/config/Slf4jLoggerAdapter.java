package co.com.bancolombia.config;

import co.com.bancolombia.model.log.gateways.LoggerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Slf4jLoggerAdapter implements LoggerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jLoggerAdapter.class);

    @Override
    public void info(String message, Object... args) {
        LOGGER.info(message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    @Override
    public void error(String message, JsonProcessingException id, String eMessage, String throwable) {
        LOGGER.error(message, throwable);
    }
}
