package co.com.bancolombia.usecase.createloanapplication;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.usecase.constants.LoanUseCaseConstants;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RequiredArgsConstructor
public class CreateLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final ClientValidationGateway clientValidationGateway;
    private final LoggerService logger;

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        logger.info(LoanUseCaseConstants.LOG_INIT_CREATE_APP, loanApplication.getDocumentNumber());

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    var auth = securityContext.getAuthentication();
                    String tokenDocumentNumber = auth.getCredentials().toString();

                    if (!tokenDocumentNumber.equals(loanApplication.getDocumentNumber())) {
                        logger.warn(LoanUseCaseConstants.LOG_WARN_UNAUTHORIZED_OPERATION,
                                tokenDocumentNumber, loanApplication.getDocumentNumber());
                        return Mono.error(new BusinessException(LoanUseCaseConstants.ERROR_UNAUTHORIZED_CLIENT_OPERATION));
                    }


                    Mono<Long> clientIdMono = clientValidationGateway.findClientIdByDocumentNumber(loanApplication.getDocumentNumber())
                            .doOnNext(clientId -> logger.info(LoanUseCaseConstants.LOG_CLIENT_FOUND, clientId))
                            .switchIfEmpty(Mono.error(new BusinessException(LoanUseCaseConstants.ERROR_CLIENT_NOT_FOUND)));

                    Mono<Boolean> loanTypeExistsMono = loanTypeRepository.existsById(loanApplication.getLoanTypeId())
                            .filter(Boolean::booleanValue)
                            .switchIfEmpty(Mono.defer(() -> {
                                logger.warn(LoanUseCaseConstants.LOG_LOAN_TYPE_INVALID, loanApplication.getLoanTypeId());
                                return Mono.error(new BusinessException(LoanUseCaseConstants.ERROR_LOAN_TYPE_NOT_FOUND));
                            }));

                    return Mono.zip(clientIdMono, loanTypeExistsMono)
                            .flatMap(tuple -> {
                                Long clientId = tuple.getT1();
                                logger.info(LoanUseCaseConstants.LOG_SAVING_APP);

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