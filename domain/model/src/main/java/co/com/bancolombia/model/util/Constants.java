package co.com.bancolombia.model.util;

public final class Constants {
    
    private Constants() {
    }

    // --- Códigos de Error ---
    public static final String LOAN_NOT_FOUND_CODE = "LOAN001";
    public static final String INVALID_LOAN_STATUS_CODE = "LOAN002";
    public static final String UNAUTHORIZED_ROLE_CODE = "AUTH001";
    public static final String NOTIFICATION_SEND_ERROR_CODE = "NOTIF001";
    public static final String INVALID_STATUS_VALUE_CODE = "LOAN003";

    // Códigos de Error específicos para CreateLoanApplicationUseCase
    public static final String UNAUTHORIZED_CLIENT_OPERATION_CODE = "AUTH002";
    public static final String CLIENT_NOT_FOUND_CODE = "CLI001";
    public static final String LOAN_TYPE_NOT_FOUND_CODE = "LOAN005";


    // --- Mensajes de Error ---
    public static final String LOAN_NOT_FOUND_MSG = "Loan application with ID %s not found.";
    public static final String INVALID_LOAN_STATUS_MSG = "Cannot change loan status from %s to %s.";
    public static final String UNAUTHORIZED_ROLE_MSG = "User with role %s is not authorized for this operation.";
    public static final String NOTIFICATION_SEND_ERROR_MSG = "Failed to send notification for loan %s.";
    public static final String INVALID_STATUS_VALUE_MSG = "Invalid status provided. Only '%s' or '%s' are allowed.";

    // Mensajes de Error específicos para CreateLoanApplicationUseCase
    public static final String ERROR_UNAUTHORIZED_CLIENT_OPERATION_MSG = "Document number from token (%s) does not match document number in request (%s). Unauthorized operation.";
    public static final String ERROR_CLIENT_NOT_FOUND_MSG = "Client with document number %s not found.";
    public static final String ERROR_LOAN_TYPE_NOT_FOUND_MSG = "Loan type with ID %s not found.";

    // --- Mensajes de Log ---
    public static final String LOG_INIT_CREATE_APP = "Solicitud de creación de préstamo recibida para documento: {}";
    public static final String LOG_WARN_UNAUTHORIZED_OPERATION = "Operación no autorizada. Documento de token: {}, Documento en solicitud: {}";
    public static final String LOG_CLIENT_FOUND = "Cliente encontrado con ID: {}";
    public static final String LOG_LOAN_TYPE_INVALID = "Tipo de préstamo no encontrado con ID: {}";
    public static final String LOG_SAVING_APP = "Guardando solicitud de préstamo...";

    // --- Roles ---
    public static final String ROLE_ADVISER = "ADVISER";
    public static final String ROLE_CLIENT = "CLIENT";
    public static final String ROLE_ADMIN = "ADMIN";
}
