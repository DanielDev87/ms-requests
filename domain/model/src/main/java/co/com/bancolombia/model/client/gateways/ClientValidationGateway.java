package co.com.bancolombia.model.client.gateways;

import reactor.core.publisher.Mono;

public interface ClientValidationGateway {
    Mono<Boolean> clientExists(String documentNumber);

    Mono<Long> findClientIdByDocumentNumber(String documentNumber);
}
