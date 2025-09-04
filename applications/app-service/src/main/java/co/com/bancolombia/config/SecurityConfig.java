package co.com.bancolombia.config;

import co.com.bancolombia.securityprovider.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationWebFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(SecurityConstants.SWAGGER_PUBLIC_PATHS).permitAll()
                        // Regla para que CLIENTES creen solicitudes
                        .pathMatchers(HttpMethod.POST, SecurityConstants.LOAN_REQUESTS_PATH)
                        .hasAuthority(SecurityConstants.ROLE_CLIENT)
                        // Regla para que ASESORES lean la lista de solicitudes
                        .pathMatchers(HttpMethod.GET, SecurityConstants.LOAN_REQUESTS_PATH)
                        .hasAuthority(SecurityConstants.ROLE_ADVISER)
                        // ---------------------------------
                        .anyExchange().authenticated()
                )
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
