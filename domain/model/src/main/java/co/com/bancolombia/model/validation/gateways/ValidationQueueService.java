package co.com.bancolombia.model.validation.gateways;

import co.com.bancolombia.model.validation.ValidationRequestMessage;
import reactor.core.publisher.Mono;

public interface ValidationQueueService {
    Mono<Void> sendValidationRequest(ValidationRequestMessage message);
}
