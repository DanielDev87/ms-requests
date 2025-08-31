package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.api.handler.Handler;
import co.com.bancolombia.api.mapper.LoanApplicationApiMapper;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.usecase.createloanapplication.CreateLoanApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ExtendWith(SpringExtension.class)
// --- 1. AJUSTE CLAVE ---
// Le decimos a Spring que cargue tanto el Router COMO el Handler
@ContextConfiguration(classes = {RouterRest.class, Handler.class})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    // --- 2. MOCKEAMOS LAS DEPENDENCIAS DEL HANDLER ---
    // Ya que cargamos el Handler real, mockeamos lo que necesita para funcionar.
    @MockBean
    private CreateLoanApplicationUseCase createLoanApplicationUseCase;
    @MockBean
    private LoanApplicationApiMapper mapper;
    @MockBean
    private TransactionalOperator transactionalOperator;
    @MockBean
    private LoggerService loggerService;

    @Test
    void shouldRouteToCreateLoanApplication() {
        // Arrange (Organizar)
        LoanApplicationDTO requestDTO = new LoanApplicationDTO("123", BigDecimal.TEN, 12, 1L);
        LoanApplication mappedModel = LoanApplication.builder().documentNumber("123").build();
        LoanApplication savedModel = LoanApplication.builder().id(1L).build();

        // Simulamos el comportamiento de las dependencias del Handler
        when(mapper.toModel(any(LoanApplicationDTO.class))).thenReturn(mappedModel);
        when(createLoanApplicationUseCase.execute(any(LoanApplication.class))).thenReturn(Mono.just(savedModel));
        // Simulamos el operador transaccional para que devuelva el Mono sin alterarlo
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // Act & Assert (Actuar y Afirmar)
        webTestClient.post()
                .uri("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDTO))
                .exchange()
                .expectStatus().isCreated() // Verificamos que el estado de la respuesta sea 201 Created
                .expectBody(LoanApplication.class)
                .isEqualTo(savedModel);
    }
}