package co.com.bancolombia.consumer;

import co.com.bancolombia.consumer.config.RestConsumerConstants;
import co.com.bancolombia.consumer.dto.ClientData;
import co.com.bancolombia.model.client.Client;
import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ClientValidationAdapter implements ClientValidationGateway {

    private final WebClient webClient;

    public ClientValidationAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Boolean> clientExists(String documentNumber) {
        return null;
    }

    @Override
    public Mono<Client> findClientByDocumentNumber(String documentNumber) {
        return Mono.deferContextual(contextView -> {
            ServerWebExchange exchange = contextView.get(ServerWebExchange.class);
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            return this.webClient.get()
                    .uri(RestConsumerConstants.FIND_USER_BY_DOCUMENT_PATH, documentNumber)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(ClientData.class)
                    .map(this::toDomain);
        });
    }

    @Override
    public Mono<Long> findClientIdByDocumentNumber(String documentNumber) {
        return this.webClient.get()
                .uri(RestConsumerConstants.FIND_CLIENT_ID_BY_DOCUMENT_PATH, documentNumber)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .map(UserDTO::getId)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty());
    }

    private Client toDomain(ClientData data) {
        return Client.builder()
                .id(data.getId())
                .documentNumber(data.getDocumentNumber())
                .firstName(data.getFirstName())
                .lastName(data.getLastName())
                .email(data.getEmail())
                .baseSalary(data.getBaseSalary())
                .build();
    }
}