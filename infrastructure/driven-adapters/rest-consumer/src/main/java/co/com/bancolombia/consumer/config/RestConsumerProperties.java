package co.com.bancolombia.consumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "adapters.restconsumer")
@Data
public class RestConsumerProperties {
    private String url;
    private int timeout;
}
