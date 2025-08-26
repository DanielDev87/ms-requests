package co.com.bancolombia.api.handler;


import co.com.bancolombia.usecase.createloanapplication.CreateLoanApplicationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(CreateLoanApplicationUseCase.BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessExceptions(CreateLoanApplicationUseCase.BusinessException ex) {
        Map<String, String> error = Collections.singletonMap("error", ex.getMessage());
        // Un tipo de préstamo no encontrado es un error del cliente (400)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}