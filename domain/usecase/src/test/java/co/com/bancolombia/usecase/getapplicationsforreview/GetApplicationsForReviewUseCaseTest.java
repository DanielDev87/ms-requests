package co.com.bancolombia.usecase.getapplicationsforreview;

import co.com.bancolombia.model.client.Client;
import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.loanapplication.LoanApplicationDetail;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetApplicationsForReviewUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private ClientValidationGateway clientValidationGateway;
    @Mock
    private LoggerService logger;

    @InjectMocks
    private GetApplicationsForReviewUseCase getApplicationsForReviewUseCase;

    private LoanApplicationDetail sampleApplication;
    private Client sampleClient;

    @BeforeEach
    void setUp() {
        // Datos de la solicitud que vendrían de la BD de 'ms-requests'
        sampleApplication = LoanApplicationDetail.builder()
                .documentNumber("12345")
                .amount(50000.0)
                .term(24)
                .status("PENDING")
                .loanTypeName("Préstamo de Libre Inversión")
                .interestRate(18.5)
                .build();

        // Datos del cliente que vendrían del 'ms-authentication'
        sampleClient = Client.builder()
                .id(1L)
                .documentNumber("12345")
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@test.com")
                .baseSalary(new BigDecimal("2500000"))
                .build();
    }

    @Test
    void shouldGetAndEnrichApplicationsSuccessfully() {
        // Arrange
        int page = 0;
        int size = 10;

        // 1. Simular que el repositorio local encuentra una solicitud
        when(loanApplicationRepository.findApplicationsForReview(page, size))
                .thenReturn(Flux.just(sampleApplication));

        // 2. Simular que el gateway externo encuentra los datos del cliente para esa solicitud
        when(clientValidationGateway.findClientByDocumentNumber("12345"))
                .thenReturn(Mono.just(sampleClient));

        // Act
        Flux<LoanApplicationDetail> result = getApplicationsForReviewUseCase.execute(page, size);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(detail ->
                        // Verificar datos originales de la solicitud
                        detail.getDocumentNumber().equals("12345") &&
                                detail.getLoanTypeName().equals("Préstamo de Libre Inversión") &&
                                // Verificar datos enriquecidos del cliente
                                detail.getClientFullName().equals("Juan Perez") &&
                                detail.getClientEmail().equals("juan.perez@test.com") &&
                                detail.getClientBaseSalary().equals(new BigDecimal("2500000"))
                )
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoApplicationsFound() {
        // Arrange
        int page = 0;
        int size = 10;

        // Simular que el repositorio local no encuentra ninguna solicitud
        when(loanApplicationRepository.findApplicationsForReview(page, size))
                .thenReturn(Flux.empty());

        // Act
        Flux<LoanApplicationDetail> result = getApplicationsForReviewUseCase.execute(page, size);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}
