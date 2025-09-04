package co.com.bancolombia.consumer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class RestConsumerConfig {

    private final RestConsumerProperties properties;

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }
}