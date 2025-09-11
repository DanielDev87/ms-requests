package co.com.bancolombia.usecase.createloanapplication;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.security.gateways.SecurityContextGateway;
import co.com.bancolombia.model.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private LoanTypeRepository loanTypeRepository;
    @Mock
    private ClientValidationGateway clientValidationGateway;
    @Mock
    private LoggerService logger;
    @Mock
    private SecurityContextGateway securityContextGateway;

    @InjectMocks
    private CreateLoanApplicationUseCase createLoanApplicationUseCase;

    private LoanApplication loanApplicationRequest;
    private final String DOCUMENT_NUMBER = "12345";
    private final String DIFFERENT_DOCUMENT_NUMBER = "99999";
    private final Long CLIENT_ID = 101L;
    private final Long LOAN_TYPE_ID = 1L;

    @BeforeEach
    void setUp() {
        loanApplicationRequest = LoanApplication.builder()
                .documentNumber(DOCUMENT_NUMBER)
                .amount(new BigDecimal("10000.00"))
                .term(12)
                .loanTypeId(LOAN_TYPE_ID)
                .build();
    }

    @Test
    @DisplayName("Debería crear una solicitud de préstamo cuando todas las verificaciones pasan")
    void shouldCreateApplicationWhenAllChecksPass() {
        // Arrange
        when(securityContextGateway.getAuthenticatedUserDocumentNumber()).thenReturn(Mono.just(DOCUMENT_NUMBER));
        when(clientValidationGateway.findClientIdByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(Mono.just(CLIENT_ID));
        when(loanTypeRepository.existsById(LOAN_TYPE_ID)).thenReturn(Mono.just(true));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenAnswer(invocation -> {
                    LoanApplication appToSave = invocation.getArgument(0);

                    return Mono.just(appToSave.toBuilder().id(1L).build());
                });

        // Act & Assert
        StepVerifier.create(createLoanApplicationUseCase.execute(loanApplicationRequest))
                .expectNextMatches(savedApp -> savedApp.getClientId().equals(CLIENT_ID) &&
                        savedApp.getStatus() == LoanApplication.Status.PENDING &&
                        savedApp.getRequestDate().isEqual(LocalDate.now()) &&
                        savedApp.getId() != null)
                .verifyComplete();

        // Verificaciones de interacciones
        verify(logger, times(1)).info(eq(Constants.LOG_INIT_CREATE_APP), eq(DOCUMENT_NUMBER));
        verify(securityContextGateway, times(1)).getAuthenticatedUserDocumentNumber();
        verify(clientValidationGateway, times(1)).findClientIdByDocumentNumber(eq(DOCUMENT_NUMBER));
        verify(logger, times(1)).info(eq(Constants.LOG_CLIENT_FOUND), eq(CLIENT_ID));
        verify(loanTypeRepository, times(1)).existsById(eq(LOAN_TYPE_ID));
        verify(logger, times(1)).info(eq(Constants.LOG_SAVING_APP));
        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería lanzar error si el documento del token no coincide con el de la solicitud")
    void shouldReturnErrorWhenTokenDocumentDoesNotMatchRequestDocument() {
        // Arrange
        when(securityContextGateway.getAuthenticatedUserDocumentNumber()).thenReturn(Mono.just(DIFFERENT_DOCUMENT_NUMBER));

        // Act & Assert
        StepVerifier.create(createLoanApplicationUseCase.execute(loanApplicationRequest))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getCode().equals(Constants.UNAUTHORIZED_CLIENT_OPERATION_CODE) &&
                        e.getMessage().contains(DIFFERENT_DOCUMENT_NUMBER) &&
                        e.getMessage().contains(DOCUMENT_NUMBER))
                .verify();


        verify(logger, times(1)).info(eq(Constants.LOG_INIT_CREATE_APP), eq(DOCUMENT_NUMBER));
        verify(securityContextGateway, times(1)).getAuthenticatedUserDocumentNumber();
        verify(logger, times(1)).warn(eq(Constants.LOG_WARN_UNAUTHORIZED_OPERATION), eq(DIFFERENT_DOCUMENT_NUMBER), eq(DOCUMENT_NUMBER));

        verify(clientValidationGateway, never()).findClientIdByDocumentNumber(any());
        verify(loanTypeRepository, never()).existsById(anyLong());
        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería lanzar error si el cliente no es encontrado")
    void shouldReturnErrorWhenClientNotFound() {
        // Arrange
        when(securityContextGateway.getAuthenticatedUserDocumentNumber()).thenReturn(Mono.just(DOCUMENT_NUMBER));
        when(clientValidationGateway.findClientIdByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(Mono.empty());
        when(loanTypeRepository.existsById(LOAN_TYPE_ID)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(createLoanApplicationUseCase.execute(loanApplicationRequest))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getCode().equals(Constants.CLIENT_NOT_FOUND_CODE) &&
                        e.getMessage().contains(DOCUMENT_NUMBER))
                .verify();

        // Verificaciones de interacciones
        verify(logger, times(1)).info(eq(Constants.LOG_INIT_CREATE_APP), eq(DOCUMENT_NUMBER));
        verify(securityContextGateway, times(1)).getAuthenticatedUserDocumentNumber();
        verify(clientValidationGateway, times(1)).findClientIdByDocumentNumber(eq(DOCUMENT_NUMBER));

        verify(logger, never()).info(eq(Constants.LOG_CLIENT_FOUND), any());

        verify(loanTypeRepository, times(1)).existsById(eq(LOAN_TYPE_ID));

        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería lanzar error si el tipo de préstamo no es encontrado")
    void shouldReturnErrorWhenLoanTypeNotFound() {
        // Arrange
        when(securityContextGateway.getAuthenticatedUserDocumentNumber()).thenReturn(Mono.just(DOCUMENT_NUMBER));
        when(clientValidationGateway.findClientIdByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(Mono.just(CLIENT_ID));
        when(loanTypeRepository.existsById(LOAN_TYPE_ID)).thenReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(createLoanApplicationUseCase.execute(loanApplicationRequest))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getCode().equals(Constants.LOAN_TYPE_NOT_FOUND_CODE) &&
                        e.getMessage().contains(String.valueOf(LOAN_TYPE_ID)))
                .verify();

        // Verificaciones de interacciones
        verify(logger, times(1)).info(eq(Constants.LOG_INIT_CREATE_APP), eq(DOCUMENT_NUMBER));
        verify(securityContextGateway, times(1)).getAuthenticatedUserDocumentNumber();
        verify(clientValidationGateway, times(1)).findClientIdByDocumentNumber(eq(DOCUMENT_NUMBER));
        verify(logger, times(1)).info(eq(Constants.LOG_CLIENT_FOUND), eq(CLIENT_ID));
        verify(loanTypeRepository, times(1)).existsById(eq(LOAN_TYPE_ID));
        verify(logger, times(1)).warn(eq(Constants.LOG_LOAN_TYPE_INVALID), eq(LOAN_TYPE_ID));

        // Verificamos que los otros servicios NO fueron llamados
        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería lanzar error si falla el guardado de la solicitud")
    void shouldReturnErrorWhenSaveFails() {
        // Arrange
        when(securityContextGateway.getAuthenticatedUserDocumentNumber()).thenReturn(Mono.just(DOCUMENT_NUMBER));
        when(clientValidationGateway.findClientIdByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(Mono.just(CLIENT_ID));
        when(loanTypeRepository.existsById(LOAN_TYPE_ID)).thenReturn(Mono.just(true));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.error(new RuntimeException("Database connection lost")));

        // Act & Assert
        StepVerifier.create(createLoanApplicationUseCase.execute(loanApplicationRequest))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().contains("Database connection lost"))
                .verify();

        // Verificaciones de interacciones
        verify(logger, times(1)).info(eq(Constants.LOG_INIT_CREATE_APP), eq(DOCUMENT_NUMBER));
        verify(securityContextGateway, times(1)).getAuthenticatedUserDocumentNumber();
        verify(clientValidationGateway, times(1)).findClientIdByDocumentNumber(eq(DOCUMENT_NUMBER));
        verify(logger, times(1)).info(eq(Constants.LOG_CLIENT_FOUND), eq(CLIENT_ID));
        verify(loanTypeRepository, times(1)).existsById(eq(LOAN_TYPE_ID));
        verify(logger, times(1)).info(eq(Constants.LOG_SAVING_APP));
        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }
}