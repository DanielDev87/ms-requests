package co.com.bancolombia.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

@Configuration
@ComponentScan(basePackages = "co.com.bancolombia.r2dbc",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class)
        },
        useDefaultFilters = false)
public class AdapterConfig {
}
