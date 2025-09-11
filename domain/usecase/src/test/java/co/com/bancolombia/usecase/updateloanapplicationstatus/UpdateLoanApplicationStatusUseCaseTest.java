package co.com.bancolombia.usecase.updateloanapplicationstatus;

import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.LoanApplication.Status;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.notificationmessage.NotificationMessage;
import co.com.bancolombia.model.notificationmessage.gateways.NotificationService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateLoanApplicationStatusUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private SecurityContextGateway securityContextGateway;
    @Mock
    private NotificationService notificationService;
    @Mock
    private LoggerService logger;

    @InjectMocks
    private UpdateLoanApplicationStatusUseCase updateLoanApplicationStatusUseCase;

    private LoanApplication pendingLoanApplication;
    private LoanApplication approvedLoanApplication;
    private LoanApplication rejectedLoanApplication;
    private static final Long LOAN_ID = 1L;
    private static final Long CLIENT_ID = 100L;
    private static final String CLIENT_DOCUMENT = "123456789";

    @BeforeEach
    void setUp() {
        pendingLoanApplication = LoanApplication.builder()
                .id(LOAN_ID)
                .clientId(CLIENT_ID)
                .documentNumber(CLIENT_DOCUMENT)
                .amount(new BigDecimal("1000.00"))
                .term(12)
                .status(Status.PENDING)
                .requestDate(LocalDate.now())
                .build();

        approvedLoanApplication = pendingLoanApplication.toBuilder().status(Status.APPROVED).build();
        rejectedLoanApplication = pendingLoanApplication.toBuilder().status(Status.REJECTED).build();
    }

    @Test
    @DisplayName("Debería aprobar una solicitud pendiente por un asesor y enviar notificación")
    void shouldApprovePendingApplicationByAdviserAndSendNotification() {
        when(securityContextGateway.getAuthenticatedUserRole()).thenReturn(Mono.just(Constants.ROLE_ADVISER));
        when(loanApplicationRepository.findById(LOAN_ID)).thenReturn(Mono.just(pendingLoanApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(Mono.just(approvedLoanApplication));
        when(notificationService.sendNotification(any(NotificationMessage.class))).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanApplicationStatusUseCase.updateStatus(LOAN_ID, "APPROVED", "Aprobado por revisor"))
                .expectNextMatches(app -> app.getId().equals(LOAN_ID) && app.getStatus().equals(Status.APPROVED))
                .verifyComplete();

        verify(loanApplicationRepository).save(any(LoanApplication.class));
        verify(notificationService).sendNotification(any(NotificationMessage.class));
    }

    @Test
    @DisplayName("Debería rechazar una solicitud pendiente por un asesor y enviar notificación")
    void shouldRejectPendingApplicationByAdviserAndSendNotification() {
        when(securityContextGateway.getAuthenticatedUserRole()).thenReturn(Mono.just(Constants.ROLE_ADVISER));
        when(loanApplicationRepository.findById(LOAN_ID)).thenReturn(Mono.just(pendingLoanApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(Mono.just(rejectedLoanApplication));
        when(notificationService.sendNotification(any(NotificationMessage.class))).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanApplicationStatusUseCase.updateStatus(LOAN_ID, "REJECTED", "Rechazado por política interna"))
                .expectNextMatches(app -> app.getId().equals(LOAN_ID) && app.getStatus().equals(Status.REJECTED))
                .verifyComplete();

        verify(loanApplicationRepository).save(any(LoanApplication.class));
        verify(notificationService).sendNotification(any(NotificationMessage.class));
    }

    @Test
    @DisplayName("Debería lanzar error si el usuario no es ASESOR")
    void shouldThrowErrorIfUserIsNotAdviser() {
        String unauthorizedRole = "CLIENT";
        when(securityContextGateway.getAuthenticatedUserRole()).thenReturn(Mono.just(unauthorizedRole));

        StepVerifier.create(updateLoanApplicationStatusUseCase.updateStatus(LOAN_ID, "APPROVED", "Comentarios"))
                .expectErrorMatches(e -> e instanceof BusinessException && ((BusinessException) e).getCode().equals(Constants.UNAUTHORIZED_ROLE_CODE))
                .verify();

        verify(loanApplicationRepository, never()).findById(anyLong());
        verify(logger).warn(eq("Usuario con rol '{}' intentó actualizar solicitud {}. No autorizado."), eq(unauthorizedRole), eq(LOAN_ID));
    }

    @Test
    @DisplayName("Debería lanzar error si la solicitud no existe")
    void shouldThrowErrorIfApplicationNotFound() {
        when(securityContextGateway.getAuthenticatedUserRole()).thenReturn(Mono.just(Constants.ROLE_ADVISER));
        when(loanApplicationRepository.findById(LOAN_ID)).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanApplicationStatusUseCase.updateStatus(LOAN_ID, "APPROVED", "Comentarios"))
                .expectErrorMatches(e -> e instanceof BusinessException && ((BusinessException) e).getCode().equals(Constants.LOAN_NOT_FOUND_CODE))
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar error si el estado ya no es PENDING")
    void shouldThrowErrorIfApplicationIsNotPending() {
        when(securityContextGateway.getAuthenticatedUserRole()).thenReturn(Mono.just(Constants.ROLE_ADVISER));
        when(loanApplicationRepository.findById(LOAN_ID)).thenReturn(Mono.just(approvedLoanApplication));

        StepVerifier.create(updateLoanApplicationStatusUseCase.updateStatus(LOAN_ID, "REJECTED", "Comentarios"))
                .expectErrorMatches(e -> e instanceof BusinessException && ((BusinessException) e).getCode().equals(Constants.INVALID_LOAN_STATUS_CODE))
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar error si el nuevo estado es inválido")
    void shouldThrowErrorIfNewStatusIsInvalid() {
        when(securityContextGateway.getAuthenticatedUserRole()).thenReturn(Mono.just(Constants.ROLE_ADVISER));

        StepVerifier.create(updateLoanApplicationStatusUseCase.updateStatus(LOAN_ID, "INVALID_STATUS", "Comentarios"))
                .expectErrorMatches(e -> e instanceof BusinessException && ((BusinessException) e).getCode().equals(Constants.INVALID_STATUS_VALUE_CODE))
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar error si falla el envío de la notificación")
    void shouldThrowErrorOnNotificationSend() {
        when(securityContextGateway.getAuthenticatedUserRole()).thenReturn(Mono.just(Constants.ROLE_ADVISER));
        when(loanApplicationRepository.findById(LOAN_ID)).thenReturn(Mono.just(pendingLoanApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(Mono.just(approvedLoanApplication));
        when(notificationService.sendNotification(any(NotificationMessage.class)))
                .thenReturn(Mono.error(new RuntimeException("SQS is down")));

        StepVerifier.create(updateLoanApplicationStatusUseCase.updateStatus(LOAN_ID, "APPROVED", "Comentarios"))
                .expectErrorMatches(e -> e instanceof BusinessException && ((BusinessException) e).getCode().equals(Constants.NOTIFICATION_SEND_ERROR_CODE))
                .verify();
    }
}