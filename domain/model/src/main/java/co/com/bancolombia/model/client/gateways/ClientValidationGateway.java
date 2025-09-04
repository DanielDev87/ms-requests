package co.com.bancolombia.model.client.gateways;

import co.com.bancolombia.model.client.Client;
import reactor.core.publisher.Mono;

public interface ClientValidationGateway {
    Mono<Boolean> clientExists(String documentNumber);

    Mono<Long> findClientIdByDocumentNumber(String documentNumber);

    Mono<Client> findClientByDocumentNumber(String documentNumber);
}
