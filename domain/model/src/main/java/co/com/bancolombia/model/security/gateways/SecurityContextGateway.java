package co.com.bancolombia.model.security.gateways;

import reactor.core.publisher.Mono;

public interface SecurityContextGateway {
    Mono<String> getAuthenticatedUserDocumentNumber();
    Mono<Object> getAuthenticatedUserRole();
}