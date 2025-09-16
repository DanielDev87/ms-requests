package co.com.bancolombia.usecase.createloanapplication;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.security.gateways.SecurityContextGateway;
import co.com.bancolombia.model.validation.ValidationRequestMessage;
import co.com.bancolombia.model.validation.gateways.ValidationQueueService;
import co.com.bancolombia.usecase.constants.LoanUseCaseConstants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RequiredArgsConstructor
public class CreateLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final ClientValidationGateway clientValidationGateway;
    private final LoggerService logger;
    private final SecurityContextGateway securityContextGateway;
    private final ValidationQueueService validationQueueService;

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        logger.info(LoanUseCaseConstants.LOG_INIT_CREATE_APP, loanApplication.getDocumentNumber());

        return securityContextGateway.getAuthenticatedUserDocumentNumber()
                .flatMap(tokenDocumentNumber -> {
                    if (!tokenDocumentNumber.equals(loanApplication.getDocumentNumber())) {
                        logger.warn(LoanUseCaseConstants.LOG_WARN_UNAUTHORIZED_OPERATION,
                                tokenDocumentNumber, loanApplication.getDocumentNumber());
                        return Mono.error(BusinessException.unauthorizedClientOperation(tokenDocumentNumber, loanApplication.getDocumentNumber()));
                    }

                    Mono<LoanType> loanTypeMono = loanTypeRepository.findById(loanApplication.getLoanTypeId())
                            .switchIfEmpty(Mono.defer(() -> {
                                logger.warn(LoanUseCaseConstants.LOG_LOAN_TYPE_INVALID, loanApplication.getLoanTypeId());
                                return Mono.error(BusinessException.loanTypeNotFound(loanApplication.getLoanTypeId()));
                            }));

                    Mono<Long> clientIdMono = clientValidationGateway.findClientIdByDocumentNumber(loanApplication.getDocumentNumber())
                            .doOnNext(clientId -> logger.info(LoanUseCaseConstants.LOG_CLIENT_FOUND, clientId))
                            .switchIfEmpty(Mono.error(BusinessException.clientNotFound(loanApplication.getDocumentNumber())));

                    return Mono.zip(clientIdMono, loanTypeMono)
                            .flatMap(tuple -> {
                                Long clientId = tuple.getT1();
                                LoanType loanType = tuple.getT2();
                                logger.info(LoanUseCaseConstants.LOG_SAVING_APP);

                                LoanApplication applicationToSave = loanApplication.toBuilder()
                                        .clientId(clientId)
                                        .status(LoanApplication.Status.PENDING)
                                        .requestDate(LocalDate.now())
                                        .build();

                                return loanApplicationRepository.save(applicationToSave)
                                        .flatMap(savedApplication -> {
                                            // Verificar validación automática
                                            if (loanType.isAutomaticValidation()) {
                                                logger.info(LoanUseCaseConstants.LOG_AUTO_VALIDATION_REQUIRED,
                                                        savedApplication.getId(), loanType.getId(), loanType.getName());

                                                ValidationRequestMessage validationMessage = ValidationRequestMessage.builder()
                                                        .loanApplicationId(savedApplication.getId())
                                                        .clientId(savedApplication.getClientId())
                                                        .documentNumber(savedApplication.getDocumentNumber())
                                                        .amount(savedApplication.getAmount())
                                                        .term(savedApplication.getTerm())
                                                        .interestRate(loanType.getInterestRate())
                                                        .build();

                                                return validationQueueService.sendValidationRequest(validationMessage)
                                                        .thenReturn(savedApplication)
                                                        .doOnError(e -> logger.error(LoanUseCaseConstants.LOG_ERROR_ENQUEUING_VALIDATION_MESSAGE,
                                                                savedApplication.getId(), e.getMessage(), e));
                                            } else {
                                                logger.info(LoanUseCaseConstants.LOG_AUTO_VALIDATION_NOT_REQUIRED,
                                                        savedApplication.getId(), loanType.getId(), loanType.getName());
                                                return Mono.just(savedApplication);
                                            }
                                        });
                            });
                });
    }
}