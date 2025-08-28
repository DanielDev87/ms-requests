package co.com.bancolombia.consumer;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class ClientValidationAdapter implements ClientValidationGateway {

    private final WebClient webClient;

    // The scaffold injects the configured WebClient for you
    public ClientValidationAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Boolean> clientExists(String documentNumber) {
        return null;
    }

    @Override
    public Mono<Long> findClientIdByDocumentNumber(String documentNumber) {
        return this.webClient.get()
                .uri("/document/{documentNumber}", documentNumber)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .map(UserDTO::getId)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty());
    }
}