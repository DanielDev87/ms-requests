package co.com.bancolombia.api.handler;

import co.com.bancolombia.model.exceptions.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof BusinessException) {
            log.warn("Error de negocio: {}", ex.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            // Pasamos 'exchange' como parámetro
            return prepareErrorResponse(exchange, bufferFactory, Collections.singletonMap("error", ex.getMessage()));
        }

        if (ex instanceof WebExchangeBindException bindException) {
            log.warn("Error de validación en la petición: {}", bindException.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            Map<String, String> errors = new HashMap<>();
            bindException.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            // Pasamos 'exchange' como parámetro
            return prepareErrorResponse(exchange, bufferFactory, errors);
        }

        log.error("Error inesperado en la aplicación", ex);
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        // Pasamos 'exchange' como parámetro
        return prepareErrorResponse(exchange, bufferFactory, Collections.singletonMap("error", "Ocurrió un error inesperado"));
    }

    // --- MÉTODO CORREGIDO ---
    // Ahora recibe 'exchange' como primer parámetro
    private Mono<Void> prepareErrorResponse(ServerWebExchange exchange, DataBufferFactory bufferFactory, Map<String, String> errorBody) {
        try {
            byte[] errorBytes = objectMapper.writeValueAsBytes(errorBody);
            DataBuffer dataBuffer = bufferFactory.wrap(errorBytes);
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error("Error escribiendo la respuesta de error en formato JSON", e);
            return exchange.getResponse().setComplete();
        }
    }
}