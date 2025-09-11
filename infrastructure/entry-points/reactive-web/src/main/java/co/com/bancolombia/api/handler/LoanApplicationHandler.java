package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.constants.ApiConstants;
import co.com.bancolombia.api.dto.ErrorResponseDTO;
import co.com.bancolombia.api.dto.UpdateLoanApplicationStatusRequestDTO;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.util.Constants;
import co.com.bancolombia.usecase.updateloanapplicationstatus.UpdateLoanApplicationStatusUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
                .flatMap(dto -> {
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errorMessage = violations.stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .reduce("", (acc, msg) -> acc + (acc.isEmpty() ? "" : "; ") + msg);
                        logger.warn(ApiConstants.LOG_HANDLER_VALIDATION_ERROR, errorMessage);
                        return buildErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                ApiConstants.ERROR_CODE_VALIDATION,
                                ApiConstants.ERROR_MESSAGE_VALIDATION + errorMessage
                        );
                    }

                    logger.info(ApiConstants.LOG_HANDLER_UPDATE_REQUEST_RECEIVED, dto.getId(), dto.getNewStatus());

                    return updateLoanApplicationStatusUseCase.updateStatus(dto.getId(), dto.getNewStatus(), dto.getComments())
                            .flatMap(updatedApplication ->
                                    ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(updatedApplication))
                            .onErrorResume(BusinessException.class, e -> {

                                logger.error(ApiConstants.LOG_HANDLER_BUSINESS_ERROR, dto.getId(), e.getMessage(), e.getCode());
                                return handleBusinessException(e);
                            })
                            .onErrorResume(Throwable.class, e -> {

                                logger.error(ApiConstants.LOG_HANDLER_UNEXPECTED_ERROR, dto.getId(), e.getMessage(), e);
                                return buildErrorResponse(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        ApiConstants.ERROR_CODE_INTERNAL,
                                        ApiConstants.ERROR_MESSAGE_INTERNAL
                                );
                            });
                });
    }

    private Mono<ServerResponse> handleBusinessException(BusinessException ex) {
        HttpStatus status;
        String errorMessageForClient;

        switch (ex.getCode()) {
            case Constants.UNAUTHORIZED_ROLE_CODE:
            case Constants.UNAUTHORIZED_CLIENT_OPERATION_CODE:
                status = HttpStatus.FORBIDDEN; // 403 Forbidden
                errorMessageForClient = "Operación no autorizada. Verifique sus permisos.";
                break;
            case Constants.LOAN_NOT_FOUND_CODE:
                status = HttpStatus.NOT_FOUND; // 404 Not Found
                errorMessageForClient = "La solicitud de préstamo especificada no fue encontrada.";
                break;
            case Constants.INVALID_LOAN_STATUS_CODE:
            case Constants.INVALID_STATUS_VALUE_CODE:
            case Constants.CLIENT_NOT_FOUND_CODE:
            case Constants.LOAN_TYPE_NOT_FOUND_CODE:
                status = HttpStatus.BAD_REQUEST; // 400 Bad Request
                errorMessageForClient = "La solicitud contiene datos inválidos o el estado de la aplicación no permite la actualización.";
                break;
            case Constants.NOTIFICATION_SEND_ERROR_CODE:
                status = HttpStatus.ACCEPTED; // 202 Accepted: la lógica de negocio pasó, pero la notificación falló
                errorMessageForClient = "El estado de la solicitud ha sido actualizado, pero la notificación por correo electrónico falló.";
                break;
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR; // Para cualquier otro BusinessException no mapeado
                errorMessageForClient = ApiConstants.ERROR_MESSAGE_INTERNAL;
        }

        return buildErrorResponse(status, ex.getCode(), errorMessageForClient);
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus status, String errorCode, String message) {
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ErrorResponseDTO.builder()
                        .code(errorCode)
                        .build());
    }
}