package co.com.bancolombia.config;

import co.com.bancolombia.model.log.gateways.LoggerService;
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
    public void error(String message, Object... args) {
        LOGGER.error(message, args);
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        Object[] allArgs = new Object[args.length + 1];
        System.arraycopy(args, 0, allArgs, 0, args.length);
        allArgs[args.length] = throwable;

        LOGGER.error(message, allArgs);
    }

    @Override
    public void debug(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    @Override
    public void trace(String message, Object... args) {
        LOGGER.trace(message, args);
    }
}