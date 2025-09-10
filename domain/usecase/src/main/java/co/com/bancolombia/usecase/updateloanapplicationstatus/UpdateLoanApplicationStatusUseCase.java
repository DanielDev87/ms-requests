package co.com.bancolombia.usecase.updateloanapplicationstatus;

import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.notificationmessage.NotificationMessage;
import co.com.bancolombia.model.notificationmessage.gateways.NotificationService;
import co.com.bancolombia.model.security.gateways.SecurityContextGateway;
import co.com.bancolombia.model.util.Constants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateLoanApplicationStatusUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final NotificationService notificationService;
    private final SecurityContextGateway securityContextGateway;
    private final LoggerService logger;

    public Mono<LoanApplication> updateStatus(Long loanApplicationId, String newStatusString, String comments) {
        logger.info("Inicio de actualización de estado para solicitud ID: {}, nuevo estado: {}", loanApplicationId, newStatusString);

        return securityContextGateway.getAuthenticatedUserRole()
                .flatMap(userRole -> {
                    if (!Constants.ROLE_ADVISER.equals(userRole)) {
                        logger.warn("Usuario con rol '{}' intentó actualizar solicitud {}. No autorizado.", userRole, loanApplicationId);
                        return Mono.error(BusinessException.unauthorizedRole((String) userRole));
                    }
                    return Mono.just(userRole);
                })
                .flatMap(userRole -> {
                    LoanApplication.Status newStatus;
                    try {
                        newStatus = LoanApplication.Status.valueOf(newStatusString.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        logger.warn("Estado '{}' inválido para solicitud {}.", newStatusString, loanApplicationId);
                        return Mono.error(BusinessException.invalidStatusValue(
                                LoanApplication.Status.APPROVED.name(),
                                LoanApplication.Status.REJECTED.name()
                        ));
                    }

                    if (newStatus != LoanApplication.Status.APPROVED && newStatus != LoanApplication.Status.REJECTED) {
                        logger.warn("Intento de cambiar solicitud {} a estado no permitido por este caso de uso: {}", loanApplicationId, newStatusString);
                        return Mono.error(BusinessException.invalidStatusValue(
                                LoanApplication.Status.APPROVED.name(),
                                LoanApplication.Status.REJECTED.name()
                        ));
                    }

                    return loanApplicationRepository.findById(loanApplicationId)
                            .switchIfEmpty(Mono.error(BusinessException.loanNotFound(loanApplicationId)))
                            .flatMap(existingApplication -> {
                                if (existingApplication.getStatus() != LoanApplication.Status.PENDING) {
                                    logger.warn("Solicitud {} no puede cambiar de estado de {} a {}. Debe estar en PENDING.",
                                            loanApplicationId, existingApplication.getStatus().name(), newStatus.name());
                                    return Mono.error(BusinessException.invalidLoanStatus(
                                            existingApplication.getStatus().name(),
                                            newStatus.name()
                                    ));
                                }

                                existingApplication.setStatus(newStatus);
                                // Aquí podrías añadir campos como 'decisionDate', 'advisorId', etc. si los tienes en LoanApplication
                                // existingApplication.setDecisionDate(LocalDate.now());

                                return loanApplicationRepository.save(existingApplication)
                                        .flatMap(updatedApplication -> {
                                            logger.info("Estado de solicitud {} actualizado a {}. Enviando notificación...",
                                                    updatedApplication.getId(), updatedApplication.getStatus().name());
                                            NotificationMessage notification = NotificationMessage.builder()
                                                    .loanApplicationId(updatedApplication.getId().toString())
                                                    .newStatus(updatedApplication.getStatus().name())
                                                    .applicantEmail(updatedApplication.getDocumentNumber() + "@example.com")
                                                    .applicantName("Cliente " + updatedApplication.getClientId())
                                                    .comments(comments)
                                                    .documentNumber(updatedApplication.getDocumentNumber())
                                                    .build();

                                            return notificationService.sendNotification(notification)
                                                    .doOnError(e -> logger.error("Error al enviar notificación para solicitud {}: {}",
                                                            updatedApplication.getId(), e.getMessage(), e))
                                                    .onErrorResume(e -> Mono.error(BusinessException.notificationSendError(updatedApplication.getId(), e)))
                                                    .thenReturn(updatedApplication);
                                        });
                            });
                });
    }
}
