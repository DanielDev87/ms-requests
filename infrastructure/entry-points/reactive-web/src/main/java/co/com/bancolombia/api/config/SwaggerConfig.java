package co.com.bancolombia.api.config;

import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.api.dto.LoanApplicationDetailDTO;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
                // Se añade el esquema de seguridad JWT para que Swagger lo reconozca
                .addSecurityItem(new SecurityRequirement().addList("jwt"))
                .path("/api/v1/requests", new io.swagger.v3.oas.models.PathItem()
                        // --- Documentación del POST (existente, sin cambios) ---
                        .post(new Operation()
                                .operationId("createLoanApplication")
                                .tags(List.of("Loan Application"))
                                .summary("Crear una nueva solicitud de crédito")
                                .requestBody(new RequestBody()
                                        .required(true)
                                        .content(new Content()
                                                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                        new MediaType().schema(new Schema<LoanApplicationDTO>().$ref("#/components/schemas/LoanApplicationDTO")))
                                        )
                                )
                                .responses(new ApiResponses()
                                        .addApiResponse("201", new ApiResponse().description("Solicitud creada exitosamente"))
                                        .addApiResponse("400", new ApiResponse().description("Error de validación o cliente no encontrado"))
                                )
                        )
                        // --- AÑADIDO: Documentación del GET ---
                        .get(new Operation()
                                .operationId("getApplicationsForReview")
                                .tags(List.of("Loan Application"))
                                .summary("Obtener lista de solicitudes para revisión")
                                .parameters(List.of(
                                        new Parameter().in("query").name("page").description("Número de página (inicia en 0)").schema(new Schema<>().type("integer")._default(0)),
                                        new Parameter().in("query").name("size").description("Tamaño de la página").schema(new Schema<>().type("integer")._default(10))
                                ))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse()
                                                .description("Lista de solicitudes obtenida exitosamente")
                                                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                        new MediaType().schema(new Schema<LoanApplicationDetailDTO>().$ref("#/components/schemas/LoanApplicationDetailDTO"))))
                                        )
                                        .addApiResponse("401", new ApiResponse().description("No autorizado"))
                                        .addApiResponse("403", new ApiResponse().description("Acceso denegado"))
                                )
                        )
                )
                .components(new Components()
                        // --- AÑADIDO: Definición del esquema de seguridad JWT ---
                        .addSecuritySchemes("jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                        )
                        // --- AÑADIDO: Schema para el nuevo DTO ---
                        .addSchemas("LoanApplicationDetailDTO", new Schema<LoanApplicationDetailDTO>().example(new LoanApplicationDetailDTO()))
                        // Schemas existentes
                        .addSchemas("LoanApplicationDTO", new Schema<LoanApplicationDTO>().example(new LoanApplicationDTO()))
                        .addSchemas("LoanApplication", new Schema<LoanApplication>().example(new LoanApplication()))
                );
    }
}