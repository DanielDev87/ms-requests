package co.com.bancolombia.model.exceptions;

import co.com.bancolombia.model.util.Constants;

public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String message, String code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static BusinessException loanNotFound(Long id) {
        return new BusinessException(String.format(Constants.LOAN_NOT_FOUND_MSG, id), Constants.LOAN_NOT_FOUND_CODE);
    }

    public static BusinessException invalidLoanStatus(String currentStatus, String desiredStatus) {
        return new BusinessException(String.format(Constants.INVALID_LOAN_STATUS_MSG, currentStatus, desiredStatus), Constants.INVALID_LOAN_STATUS_CODE);
    }

    public static BusinessException unauthorizedRole(String role) {
        return new BusinessException(String.format(Constants.UNAUTHORIZED_ROLE_MSG, role), Constants.UNAUTHORIZED_ROLE_CODE);
    }

    public static BusinessException notificationSendError(Long loanApplicationId, Throwable cause) {
        return new BusinessException(String.format(Constants.NOTIFICATION_SEND_ERROR_MSG, loanApplicationId), Constants.NOTIFICATION_SEND_ERROR_CODE, cause);
    }

    public static BusinessException invalidStatusValue(String approvedStatus, String rejectedStatus) {
        return new BusinessException(String.format(Constants.INVALID_STATUS_VALUE_MSG, approvedStatus, rejectedStatus), Constants.INVALID_STATUS_VALUE_CODE);
    }
    public static BusinessException unauthorizedClientOperation(String tokenDocumentNumber, String requestDocumentNumber) {
        return new BusinessException(String.format(Constants.ERROR_UNAUTHORIZED_CLIENT_OPERATION_MSG, tokenDocumentNumber, requestDocumentNumber), Constants.UNAUTHORIZED_CLIENT_OPERATION_CODE);
    }

    public static BusinessException clientNotFound(String documentNumber) {
        return new BusinessException(String.format(Constants.ERROR_CLIENT_NOT_FOUND_MSG, documentNumber), Constants.CLIENT_NOT_FOUND_CODE);
    }

    public static BusinessException loanTypeNotFound(Long loanTypeId) {
        return new BusinessException(String.format(Constants.ERROR_LOAN_TYPE_NOT_FOUND_MSG, loanTypeId), Constants.LOAN_TYPE_NOT_FOUND_CODE);
    }
}
