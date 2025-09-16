package co.com.bancolombia.usecase.updateloanapplicationstatus;

import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.notificationmessage.NotificationMessage;
import co.com.bancolombia.model.notificationmessage.gateways.NotificationService;
import co.com.bancolombia.model.security.gateways.SecurityContextGateway;
import co.com.bancolombia.model.util.Constants;
import co.com.bancolombia.usecase.constants.LoanUseCaseConstants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateLoanApplicationStatusUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final NotificationService notificationService;
    private final SecurityContextGateway securityContextGateway;
    private final LoggerService logger;

    public Mono<LoanApplication> updateStatus(Long loanApplicationId, String newStatusString, String comments) {
        logger.info(LoanUseCaseConstants.LOG_UPDATE_STATUS_INIT, loanApplicationId, newStatusString);

        return securityContextGateway.getAuthenticatedUserRole()
                .flatMap(userRole -> {
                    if (!Constants.ROLE_ADVISER.equals(userRole)) {
                        logger.warn(LoanUseCaseConstants.LOG_UNAUTHORIZED_ROLE, userRole, loanApplicationId);
                        return Mono.error(BusinessException.unauthorizedRole(userRole));
                    }
                    return Mono.just(userRole);
                })
                .flatMap(userRole -> {
                    LoanApplication.Status newStatus;
                    try {
                        newStatus = LoanApplication.Status.valueOf(newStatusString.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        logger.warn(LoanUseCaseConstants.LOG_INVALID_STATUS_VALUE, newStatusString, loanApplicationId);
                        return Mono.error(BusinessException.invalidStatusValue(
                                LoanApplication.Status.APPROVED.name(),
                                LoanApplication.Status.REJECTED.name()
                        ));
                    }

                    if (newStatus != LoanApplication.Status.APPROVED && newStatus != LoanApplication.Status.REJECTED) {
                        logger.warn(LoanUseCaseConstants.LOG_STATUS_NOT_ALLOWED, loanApplicationId, newStatusString);
                        return Mono.error(BusinessException.invalidStatusValue(
                                LoanApplication.Status.APPROVED.name(),
                                LoanApplication.Status.REJECTED.name()
                        ));
                    }

                    return loanApplicationRepository.findById(loanApplicationId)
                            .switchIfEmpty(Mono.error(BusinessException.loanNotFound(loanApplicationId)))
                            .flatMap(existingApplication -> {
                                if (existingApplication.getStatus() != LoanApplication.Status.PENDING) {
                                    logger.warn(LoanUseCaseConstants.LOG_STATUS_MUST_BE_PENDING,
                                            loanApplicationId, existingApplication.getStatus().name(), newStatus.name());
                                    return Mono.error(BusinessException.invalidLoanStatus(
                                            existingApplication.getStatus().name(),
                                            newStatus.name()
                                    ));
                                }

                                existingApplication.setStatus(newStatus);


                                return loanApplicationRepository.save(existingApplication)
                                        .flatMap(updatedApplication -> {
                                            logger.info(LoanUseCaseConstants.LOG_STATUS_UPDATED_NOTIFICATION,
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
                                                    .doOnError(e -> logger.error(LoanUseCaseConstants.LOG_NOTIFICATION_SEND_ERROR, // Usando constante
                                                            updatedApplication.getId(), e.getMessage(), e))
                                                    .onErrorResume(e -> Mono.error(BusinessException.notificationSendError(updatedApplication.getId(), e)))
                                                    .thenReturn(updatedApplication);
                                        });
                            });
                });
    }
}