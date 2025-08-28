package co.com.bancolombia.consumer.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class RestConsumerConfig {

    @Value("${adapter.restconsumer.url}") // This value comes from your application.yml
    private String url;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }
}