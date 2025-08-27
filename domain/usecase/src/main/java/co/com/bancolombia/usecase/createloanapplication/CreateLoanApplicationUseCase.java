package co.com.bancolombia.usecase.createloanapplication;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RequiredArgsConstructor
public class CreateLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoggerService logger;

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        logger.info("Iniciando creación de solicitud para cliente ID: {}", loanApplication.getClientId());
        return loanTypeRepository.existsById(loanApplication.getLoanTypeId())
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        logger.warn("El tipo de préstamo {} no existe.", loanApplication.getLoanTypeId());
                        return Mono.error(new BusinessException("El tipo de préstamo especificado no existe."));
                    }
                    logger.info("Tipo de préstamo {} válido. Guardando solicitud.", loanApplication.getLoanTypeId());
                    loanApplication.setStatus(LoanApplication.Status.PENDING);
                    loanApplication.setRequestDate(LocalDate.now());
                    return loanApplicationRepository.save(loanApplication);
                });
    }

    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }
}
