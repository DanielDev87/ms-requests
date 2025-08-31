package co.com.bancolombia.consumer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class RestConsumerConfig {

    // Ahora inyectamos el objeto de propiedades completo
    private final RestConsumerProperties properties;

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(properties.getUrl()) // Leemos la URL desde el objeto
                .build();
    }
}