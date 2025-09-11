package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.constants.ApiConstants;
import co.com.bancolombia.api.dto.UpdateLoanApplicationStatusRequestDTO;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.usecase.updateloanapplicationstatus.UpdateLoanApplicationStatusUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

    private final UpdateLoanApplicationStatusUseCase updateLoanApplicationStatusUseCase;
    private final LoggerService logger;
    private final Validator validator;

    public Mono<ServerResponse> updateStatus(ServerRequest request) {
        return request.bodyToMono(UpdateLoanApplicationStatusRequestDTO.class)
                .doOnNext(dto -> {
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errorMessage = violations.stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .reduce("", (acc, msg) -> acc + (acc.isEmpty() ? "" : "; ") + msg);
                        logger.warn(ApiConstants.LOG_HANDLER_VALIDATION_ERROR, errorMessage);
                        throw new BusinessException(ApiConstants.ERROR_MESSAGE_VALIDATION + errorMessage, ApiConstants.ERROR_CODE_VALIDATION);
                    }
                })
                .flatMap(dto -> {
                    logger.info(ApiConstants.LOG_HANDLER_UPDATE_REQUEST_RECEIVED, dto.getId(), dto.getNewStatus());
                    return updateLoanApplicationStatusUseCase.updateStatus(dto.getId(), dto.getNewStatus(), dto.getComments())
                            .flatMap(updatedApplication -> ServerResponse.ok()
                                    .bodyValue(updatedApplication))
                            .onErrorResume(BusinessException.class, e -> {
                                logger.error(ApiConstants.LOG_HANDLER_BUSINESS_ERROR, dto.getId(), e.getMessage(), e.getCode());
                                return ServerResponse.status(HttpStatus.BAD_REQUEST)
                                        .bodyValue(e.getMessage());
                            })
                            .onErrorResume(e -> {
                                logger.error(ApiConstants.LOG_HANDLER_UNEXPECTED_ERROR, dto.getId(), e.getMessage(), e);
                                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .bodyValue(ApiConstants.ERROR_MESSAGE_INTERNAL);
                            });
                });
    }
}
