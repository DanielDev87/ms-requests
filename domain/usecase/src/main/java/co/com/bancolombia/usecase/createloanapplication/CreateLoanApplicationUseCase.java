package co.com.bancolombia.usecase.createloanapplication;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.security.gateways.SecurityContextGateway;
import co.com.bancolombia.model.util.Constants;
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

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        logger.info(Constants.LOG_INIT_CREATE_APP, loanApplication.getDocumentNumber());

        return securityContextGateway.getAuthenticatedUserDocumentNumber()
                .flatMap(tokenDocumentNumber -> {
                    if (!tokenDocumentNumber.equals(loanApplication.getDocumentNumber())) {
                        logger.warn(Constants.LOG_WARN_UNAUTHORIZED_OPERATION,
                                tokenDocumentNumber, loanApplication.getDocumentNumber());
                        return Mono.error(BusinessException.unauthorizedClientOperation(tokenDocumentNumber, loanApplication.getDocumentNumber()));
                    }

                    Mono<Long> clientIdMono = clientValidationGateway.findClientIdByDocumentNumber(loanApplication.getDocumentNumber())
                            .doOnNext(clientId -> logger.info(Constants.LOG_CLIENT_FOUND, clientId))
                            .switchIfEmpty(Mono.error(BusinessException.clientNotFound(loanApplication.getDocumentNumber())));

                    Mono<Boolean> loanTypeExistsMono = loanTypeRepository.existsById(loanApplication.getLoanTypeId())
                            .filter(Boolean::booleanValue)
                            .switchIfEmpty(Mono.defer(() -> {
                                logger.warn(Constants.LOG_LOAN_TYPE_INVALID, loanApplication.getLoanTypeId());
                                return Mono.error(BusinessException.loanTypeNotFound(loanApplication.getLoanTypeId()));
                            }));

                    return Mono.zip(clientIdMono, loanTypeExistsMono)
                            .flatMap(tuple -> {
                                Long clientId = tuple.getT1();
                                logger.info(Constants.LOG_SAVING_APP);

                                LoanApplication applicationToSave = loanApplication.toBuilder()
                                        .clientId(clientId)
                                        .status(LoanApplication.Status.PENDING)
                                        .requestDate(LocalDate.now())
                                        .build();

                                return loanApplicationRepository.save(applicationToSave);
                            });
                });
    }
}