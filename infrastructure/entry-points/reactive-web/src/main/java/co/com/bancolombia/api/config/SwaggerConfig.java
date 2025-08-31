package co.com.bancolombia.api.config;

import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("ms-requests")
                .pathsToMatch("/api/v1/requests/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Solicitudes de Crédito")
                        .version("1.0")
                        .description("Microservicio para gestionar solicitudes de crédito.")
                )
                // Documentación para el endpoint POST /api/v1/requests
                .path("/api/v1/requests", new io.swagger.v3.oas.models.PathItem()
                        .post(new Operation()
                                .operationId("createLoanApplication")
                                .tags(java.util.List.of("Loan Application"))
                                .summary("Crear una nueva solicitud de crédito")
                                .requestBody(new RequestBody()
                                        .required(true)
                                        .content(new Content()
                                                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                        new MediaType().schema(new Schema<LoanApplicationDTO>().$ref("#/components/schemas/LoanApplicationDTO")))
                                        )
                                )
                                .responses(new ApiResponses()
                                        .addApiResponse("201", new ApiResponse()
                                                .description("Solicitud creada exitosamente")
                                                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                        new MediaType().schema(new Schema<LoanApplication>().$ref("#/components/schemas/LoanApplication"))))
                                        )
                                        .addApiResponse("400", new ApiResponse().description("Error de validación o cliente no encontrado"))
                                )
                        )
                )
                .components(new Components()
                        .addSchemas("LoanApplicationDTO", new Schema<LoanApplicationDTO>().example(new LoanApplicationDTO()))
                        .addSchemas("LoanApplication", new Schema<LoanApplication>().example(new LoanApplication()))
                );
    }
}