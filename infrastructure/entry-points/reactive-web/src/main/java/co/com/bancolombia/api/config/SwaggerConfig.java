package co.com.bancolombia.api.config;

import co.com.bancolombia.api.constants.ApiConstants;
import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.api.dto.LoanApplicationDetailDTO;
import co.com.bancolombia.api.dto.UpdateLoanApplicationStatusRequestDTO;
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
                // Asegúrate de que pathsToMatch incluya ambas rutas ahora
                .pathsToMatch(ApiConstants.LOAN_REQUEST_PATH + "/**", ApiConstants.LOAN_APPLICATION_STATUS_PATH.replace("/{id}", "/**"))
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(ApiConstants.SWAGGER_API_TITLE)
                        .version(ApiConstants.SWAGGER_API_VERSION)
                        .description(ApiConstants.SWAGGER_API_DESCRIPTION)
                )
                .addSecurityItem(new SecurityRequirement().addList(ApiConstants.SWAGGER_SECURITY_SCHEME_NAME))
                .path(ApiConstants.LOAN_REQUEST_PATH, new io.swagger.v3.oas.models.PathItem()
                        // --- POST /api/v1/requests (crear) ---
                        .post(new Operation()
                                .operationId(ApiConstants.SWAGGER_POST_OPERATION_ID)
                                .tags(List.of(ApiConstants.SWAGGER_TAG_LOAN_APPLICATION))
                                .summary(ApiConstants.SWAGGER_POST_SUMMARY)
                                .requestBody(new RequestBody()
                                        .required(true)
                                        .content(new Content()
                                                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                        new MediaType().schema(new Schema<LoanApplicationDTO>().$ref(ApiConstants.LOAN_REF_SCHEMA_PATH + ApiConstants.SWAGGER_SCHEMA_LOAN_APP_DTO))))
                                )
                                .responses(new ApiResponses()
                                        .addApiResponse("201", new ApiResponse().description(ApiConstants.SWAGGER_POST_RESP_201))
                                        .addApiResponse("400", new ApiResponse().description(ApiConstants.SWAGGER_POST_RESP_400))
                                )
                        )
                        .get(new Operation()
                                .operationId(ApiConstants.SWAGGER_GET_OPERATION_ID)
                                .tags(List.of(ApiConstants.SWAGGER_TAG_LOAN_APPLICATION))
                                .summary(ApiConstants.SWAGGER_GET_SUMMARY)
                                .parameters(List.of(
                                        new Parameter().in("query").name(ApiConstants.SWAGGER_GET_PARAM_PAGE).description(ApiConstants.SWAGGER_GET_PARAM_PAGE_DESC).schema(new Schema<>().type("integer")._default(0)),
                                        new Parameter().in("query").name(ApiConstants.SWAGGER_GET_PARAM_SIZE).description(ApiConstants.SWAGGER_GET_PARAM_SIZE_DESC).schema(new Schema<>().type("integer")._default(10))
                                ))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse()
                                                .description(ApiConstants.SWAGGER_GET_RESP_200)
                                                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                        new MediaType().schema(new Schema<LoanApplicationDetailDTO>().$ref(ApiConstants.LOAN_REF_SCHEMA_PATH + ApiConstants.SWAGGER_SCHEMA_LOAN_APP_DETAIL_DTO))))
                                        )
                                        .addApiResponse("401", new ApiResponse().description(ApiConstants.SWAGGER_GET_RESP_401))
                                        .addApiResponse("403", new ApiResponse().description(ApiConstants.SWAGGER_GET_RESP_403))
                                )
                        )
                )
                .path(ApiConstants.LOAN_APPLICATION_STATUS_PATH, new io.swagger.v3.oas.models.PathItem()
                        .put(new Operation()
                                .operationId(ApiConstants.SWAGGER_PUT_OPERATION_ID)
                                .tags(List.of(ApiConstants.SWAGGER_TAG_LOAN_APPLICATION))
                                .summary(ApiConstants.SWAGGER_PUT_SUMMARY)
                                .parameters(List.of(
                                        new Parameter().in("path").name(ApiConstants.SWAGGER_PUT_PARAM_ID).description(ApiConstants.SWAGGER_PUT_PARAM_ID_DESC).required(true).schema(new Schema<>().type("integer").format("int64"))
                                ))
                                .requestBody(new RequestBody()
                                        .description(ApiConstants.SWAGGER_PUT_REQ_BODY_DESC)
                                        .required(true)
                                        .content(new Content()
                                                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                        new MediaType().schema(new Schema<UpdateLoanApplicationStatusRequestDTO>().$ref(ApiConstants.LOAN_REF_SCHEMA_PATH + ApiConstants.SWAGGER_SCHEMA_UPDATE_LOAN_APP_STATUS_REQ_DTO))))
                                )
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description(ApiConstants.SWAGGER_PUT_RESP_200))
                                        .addApiResponse("400", new ApiResponse().description(ApiConstants.SWAGGER_PUT_RESP_400_BUSINESS))
                                        .addApiResponse("401", new ApiResponse().description(ApiConstants.SWAGGER_PUT_RESP_401))
                                        .addApiResponse("403", new ApiResponse().description(ApiConstants.SWAGGER_PUT_RESP_403))
                                        .addApiResponse("500", new ApiResponse().description(ApiConstants.SWAGGER_PUT_RESP_500))
                                )
                        )
                )
                .components(new Components()
                        .addSecuritySchemes(ApiConstants.SWAGGER_SECURITY_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(ApiConstants.SWAGGER_SECURITY_SCHEME_TYPE_HTTP)
                                .bearerFormat(ApiConstants.SWAGGER_SECURITY_SCHEME_FORMAT_JWT)
                                .in(SecurityScheme.In.HEADER)
                                .name(ApiConstants.SWAGGER_SECURITY_SCHEME_HEADER_NAME)
                        )
                        .addSchemas(ApiConstants.SWAGGER_SCHEMA_LOAN_APP_DETAIL_DTO, new Schema<LoanApplicationDetailDTO>().example(new LoanApplicationDetailDTO()))
                        .addSchemas(ApiConstants.SWAGGER_SCHEMA_LOAN_APP_DTO, new Schema<LoanApplicationDTO>().example(new LoanApplicationDTO()))
                        .addSchemas(ApiConstants.SWAGGER_SCHEMA_LOAN_APP, new Schema<LoanApplication>().example(new LoanApplication()))
                        .addSchemas(ApiConstants.SWAGGER_SCHEMA_UPDATE_LOAN_APP_STATUS_REQ_DTO, new Schema<UpdateLoanApplicationStatusRequestDTO>().example(new UpdateLoanApplicationStatusRequestDTO()))
                );
    }
}