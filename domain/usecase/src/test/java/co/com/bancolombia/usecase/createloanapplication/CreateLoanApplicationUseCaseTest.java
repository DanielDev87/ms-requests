package co.com.bancolombia.usecase.createloanapplication;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.security.gateways.SecurityContextGateway;
import co.com.bancolombia.usecase.constants.LoanUseCaseConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    private LoanApplication loanApplication;
    private final String DOCUMENT_NUMBER = "12345";

    @BeforeEach
    void setUp() {
        loanApplication = LoanApplication.builder()
                .documentNumber(DOCUMENT_NUMBER)
                .amount(new BigDecimal("10000.00"))
                .term(12)
                .loanTypeId(1L)
                .build();
    }

    @Test
    void shouldCreateApplicationWhenSecurityCheckPasses() {
        when(securityContextGateway.getAuthenticatedUserDocumentNumber()).thenReturn(Mono.just(DOCUMENT_NUMBER));
        when(clientValidationGateway.findClientIdByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(Mono.just(101L));
        when(loanTypeRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(createLoanApplicationUseCase.execute(loanApplication))
                .expectNextMatches(savedApp -> savedApp.getClientId().equals(101L) &&
                        savedApp.getStatus() == LoanApplication.Status.PENDING)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenTokenDocumentDoesNotMatchRequestDocument() {
        String differentDocumentNumber = "99999";
        when(securityContextGateway.getAuthenticatedUserDocumentNumber()).thenReturn(Mono.just(differentDocumentNumber));

        StepVerifier.create(createLoanApplicationUseCase.execute(loanApplication))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        throwable.getMessage().equals(LoanUseCaseConstants.ERROR_UNAUTHORIZED_CLIENT_OPERATION))
                .verify();
    }
}