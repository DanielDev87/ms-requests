package co.com.bancolombia.securityprovider;

import co.com.bancolombia.model.security.gateways.SecurityContextGateway;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextAdapter implements SecurityContextGateway {

    @Override
    public Mono<String> getAuthenticatedUserDocumentNumber() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getCredentials().toString());
    }

    @Override
    public Mono<String> getAuthenticatedUserRole() {
        return Mono.just("ADVISER");
    }
}
