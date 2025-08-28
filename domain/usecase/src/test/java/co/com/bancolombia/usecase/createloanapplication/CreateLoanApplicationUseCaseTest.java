package co.com.bancolombia.usecase.createloanapplication;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateLoanApplicationUseCaseTest {

    @Mock
    private ClientValidationGateway clientValidationGateway;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private LoanTypeRepository loanTypeRepository;
    @Mock
    private LoggerService logger;

    @InjectMocks
    private CreateLoanApplicationUseCase useCase;

    private LoanApplication applicationToCreate;
    private final String VALID_DOCUMENT = "1037123456";
    private final Long VALID_CLIENT_ID = 456L;

    @BeforeEach
    void setUp() {
        // La solicitud se crea con el número de documento
        applicationToCreate = LoanApplication.builder()
                .documentNumber(VALID_DOCUMENT)
                .loanTypeId(1L)
                .amount(new BigDecimal("10000"))
                .term(12)
                .build();
    }

    @Test
    void shouldCreateApplicationSuccessfullyWhenClientAndLoanTypeAreValid() {
        // Arrange
        LoanApplication savedApplication = applicationToCreate.toBuilder()
                .id(1L)
                .clientId(VALID_CLIENT_ID) // El ID del cliente se asigna en el proceso
                .status(LoanApplication.Status.PENDING)
                .requestDate(LocalDate.now())
                .build();

        // 1. Simula que el cliente SÍ existe y devuelve su ID
        when(clientValidationGateway.findClientIdByDocumentNumber(VALID_DOCUMENT))
                .thenReturn(Mono.just(VALID_CLIENT_ID));
        // 2. Simula que el tipo de préstamo SÍ existe
        when(loanTypeRepository.existsById(1L)).thenReturn(Mono.just(true));
        // 3. Simula el guardado exitoso
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(Mono.just(savedApplication));

        // Act
        Mono<LoanApplication> result = useCase.execute(applicationToCreate);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(app -> app.getId().equals(1L) && app.getClientId().equals(VALID_CLIENT_ID))
                .verifyComplete();

        // Verifica que todos los pasos se ejecutaron
        verify(clientValidationGateway).findClientIdByDocumentNumber(VALID_DOCUMENT);
        verify(loanTypeRepository).existsById(1L);
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void shouldReturnErrorWhenClientDoesNotExist() {
        // Arrange
        // 1. Simula que el cliente NO existe
        when(clientValidationGateway.findClientIdByDocumentNumber(VALID_DOCUMENT)).thenReturn(Mono.empty());

        // Act
        Mono<LoanApplication> result = useCase.execute(applicationToCreate);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CreateLoanApplicationUseCase.BusinessException
                        && throwable.getMessage().contains("cliente con el documento especificado no existe"))
                .verify();

        // Verifica que solo se llamó al gateway y el flujo se detuvo
        verify(clientValidationGateway).findClientIdByDocumentNumber(VALID_DOCUMENT);
        verify(loanTypeRepository, never()).existsById(any());
        verify(loanApplicationRepository, never()).save(any());
    }

    @Test
    void shouldReturnErrorWhenLoanTypeIsInvalid() {
        // Arrange
        // 1. Simula que el cliente SÍ existe
        when(clientValidationGateway.findClientIdByDocumentNumber(VALID_DOCUMENT))
                .thenReturn(Mono.just(VALID_CLIENT_ID));
        // 2. Simula que el tipo de préstamo NO existe
        when(loanTypeRepository.existsById(1L)).thenReturn(Mono.just(false));

        // Act
        Mono<LoanApplication> result = useCase.execute(applicationToCreate);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CreateLoanApplicationUseCase.BusinessException
                        && throwable.getMessage().contains("tipo de préstamo especificado no existe"))
                .verify();

        // Verifica que se validó el cliente y el tipo de préstamo, pero nunca se guardó
        verify(clientValidationGateway).findClientIdByDocumentNumber(VALID_DOCUMENT);
        verify(loanTypeRepository).existsById(1L);
        verify(loanApplicationRepository, never()).save(any());
    }
}