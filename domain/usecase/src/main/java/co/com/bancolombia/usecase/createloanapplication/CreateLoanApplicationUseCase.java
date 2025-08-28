package co.com.bancolombia.usecase.createloanapplication;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
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
    private final ClientValidationGateway clientValidationGateway;
    private final LoggerService logger;

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        logger.info("Iniciando creación de solicitud para cliente con documento: {}", loanApplication.getDocumentNumber());

        // 1. Busca el ID del cliente usando el número de documento
        return clientValidationGateway.findClientIdByDocumentNumber(loanApplication.getDocumentNumber())
                .switchIfEmpty(Mono.error(new BusinessException("El cliente con el documento especificado no existe.")))
                .flatMap(clientId -> {
                    logger.info("Cliente encontrado con ID: {}. Asignando a la solicitud.", clientId);
                    // 2. Asigna el ID del cliente al objeto de la solicitud
                    loanApplication.setClientId(clientId);

                    // 3. Continúa con la validación del tipo de préstamo
                    return loanTypeRepository.existsById(loanApplication.getLoanTypeId())
                            .flatMap(loanTypeExists -> {
                                if (Boolean.FALSE.equals(loanTypeExists)) {
                                    logger.warn("El tipo de préstamo {} no existe.", loanApplication.getLoanTypeId());
                                    return Mono.error(new BusinessException("El tipo de préstamo especificado no existe."));
                                }

                                // 4. Si es válido, guarda la solicitud
                                logger.info("Cliente y tipo de préstamo válidos. Guardando solicitud...");
                                loanApplication.setStatus(LoanApplication.Status.PENDING);
                                loanApplication.setRequestDate(LocalDate.now());
                                return loanApplicationRepository.save(loanApplication);
                            });
                });
    }

    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }
}