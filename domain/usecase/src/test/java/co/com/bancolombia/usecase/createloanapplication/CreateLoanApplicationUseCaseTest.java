package co.com.bancolombia.usecase.createloanapplication;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.usecase.constants.LoanUseCaseConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreateLoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private LoanTypeRepository loanTypeRepository;
    @Mock
    private ClientValidationGateway clientValidationGateway;
    @Mock
    private LoggerService logger;

    @InjectMocks
    private CreateLoanApplicationUseCase createLoanApplicationUseCase;

    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanApplication = LoanApplication.builder()
                .documentNumber("12345")
                .amount(new BigDecimal("10000"))
                .term(12)
                .loanTypeId(1L)
                .build();
    }

    @Test
    void shouldCreateLoanApplicationSuccessfully() {
        when(clientValidationGateway.findClientIdByDocumentNumber("12345")).thenReturn(Mono.just(101L));
        when(loanTypeRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(createLoanApplicationUseCase.execute(loanApplication))
                .expectNextMatches(savedApp -> savedApp.getClientId().equals(101L) &&
                        savedApp.getStatus() == LoanApplication.Status.PENDING)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenClientDoesNotExist() {
        when(clientValidationGateway.findClientIdByDocumentNumber("12345")).thenReturn(Mono.empty());
        when(loanTypeRepository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(createLoanApplicationUseCase.execute(loanApplication))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        throwable.getMessage().equals(LoanUseCaseConstants.ERROR_CLIENT_NOT_FOUND))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenLoanTypeDoesNotExist() {
        when(clientValidationGateway.findClientIdByDocumentNumber("12345")).thenReturn(Mono.just(101L));
        when(loanTypeRepository.existsById(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(createLoanApplicationUseCase.execute(loanApplication))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        throwable.getMessage().equals(LoanUseCaseConstants.ERROR_LOAN_TYPE_NOT_FOUND))
                .verify();
    }
}