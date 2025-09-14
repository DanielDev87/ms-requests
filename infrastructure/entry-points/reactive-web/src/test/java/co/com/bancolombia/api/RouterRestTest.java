package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.api.dto.LoanApplicationDetailDTO;
import co.com.bancolombia.api.dto.UpdateLoanApplicationStatusRequestDTO;
import co.com.bancolombia.api.handler.Handler;
import co.com.bancolombia.api.handler.LoanApplicationHandler;
import co.com.bancolombia.api.mapper.LoanApplicationApiMapper;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.LoanApplicationDetail;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.usecase.createloanapplication.CreateLoanApplicationUseCase;
import co.com.bancolombia.usecase.getapplicationsforreview.GetApplicationsForReviewUseCase;
import co.com.bancolombia.usecase.updateloanapplicationstatus.UpdateLoanApplicationStatusUseCase;
import jakarta.validation.Validator;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RouterRest.class, Handler.class, LoanApplicationHandler.class})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateLoanApplicationUseCase createLoanApplicationUseCase;
    @MockBean
    private GetApplicationsForReviewUseCase getApplicationsForReviewUseCase;
    @MockBean
    private UpdateLoanApplicationStatusUseCase updateLoanApplicationStatusUseCase;

    @MockBean
    private LoanApplicationApiMapper mapper;
    @MockBean
    private TransactionalOperator transactionalOperator;
    @MockBean
    private LoggerService loggerService;
    @MockBean
    private Validator validator;

    @Test
    void shouldRouteToCreateLoanApplication() {
        // Arrange
        var requestDTO = new LoanApplicationDTO("123", BigDecimal.TEN, 12, 1L);
        var mappedModel = LoanApplication.builder()
                .documentNumber(requestDTO.getDocumentNumber())
                .amount(requestDTO.getAmount())
                .term(requestDTO.getTerm())
                .loanTypeId(requestDTO.getLoanTypeId())
                .build();
        var savedModel = mappedModel.toBuilder()
                .id(1L)
                .clientId(101L)
                .status(LoanApplication.Status.PENDING)
                .requestDate(LocalDate.now())
                .build();

        when(mapper.toModel(any(LoanApplicationDTO.class))).thenReturn(mappedModel);
        when(createLoanApplicationUseCase.execute(any(LoanApplication.class))).thenReturn(Mono.just(savedModel));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDTO))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LoanApplication.class)
                .isEqualTo(savedModel);
    }

    @Test
    void shouldRouteToGetApplicationsForReview() {
        // Arrange
        var domainModel = LoanApplicationDetail.builder()
                .clientFullName("Juan Valdez")
                .clientEmail("juan@valdez.com")
                .amount(50000.00)
                .build();
        when(getApplicationsForReviewUseCase.execute(anyInt(), anyInt())).thenReturn(Flux.just(domainModel));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/requests?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].clientFullName").isEqualTo("Juan Valdez");
    }

    @Test
    void shouldRouteToUpdateStatus() {
        // Arrange
        var requestDTO = new UpdateLoanApplicationStatusRequestDTO(1L, "APPROVED", "OK");
        var updatedModel = LoanApplication.builder()
                .id(1L)
                .status(LoanApplication.Status.APPROVED)
                .build();

        when(validator.validate(any(UpdateLoanApplicationStatusRequestDTO.class))).thenReturn(Collections.emptySet());
        when(updateLoanApplicationStatusUseCase.updateStatus(anyLong(), anyString(), anyString())).thenReturn(Mono.just(updatedModel));

        // Act & Assert
        webTestClient.put()
                .uri("/api/v1/loan-applications/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDTO))
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoanApplication.class)
                .isEqualTo(updatedModel);
    }
}