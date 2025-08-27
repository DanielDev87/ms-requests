package co.com.bancolombia.usecase.createloanapplication;

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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateLoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private LoanTypeRepository loanTypeRepository;
    @Mock
    private LoggerService logger; // Mock del logger

    @InjectMocks
    private CreateLoanApplicationUseCase useCase;

    private LoanApplication application;

    @BeforeEach
    void setUp() {
        application = LoanApplication.builder()
                .clientId(123L)
                .loanTypeId(1L)
                .amount(new BigDecimal("10000"))
                .term(12)
                .build();
    }

    @Test
    void shouldSaveApplicationWhenLoanTypeIsValid() {
        // Arrange
        LoanApplication savedApplication = application.toBuilder()
                .id(1L)
                .status(LoanApplication.Status.PENDING)
                .requestDate(LocalDate.now())
                .build();

        when(loanTypeRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(Mono.just(savedApplication));

        // Act
        Mono<LoanApplication> result = useCase.execute(application);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(app -> app.getId().equals(1L) && app.getStatus() == LoanApplication.Status.PENDING)
                .verifyComplete();

        verify(loanTypeRepository).existsById(1L);
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void shouldReturnErrorWhenLoanTypeIsInvalid() {
        // Arrange
        when(loanTypeRepository.existsById(anyLong())).thenReturn(Mono.just(false));

        // Act
        Mono<LoanApplication> result = useCase.execute(application);

        // Assert
        StepVerifier.create(result)
                .expectError(CreateLoanApplicationUseCase.BusinessException.class)
                .verify();

        verify(loanTypeRepository).existsById(anyLong());
        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }
}