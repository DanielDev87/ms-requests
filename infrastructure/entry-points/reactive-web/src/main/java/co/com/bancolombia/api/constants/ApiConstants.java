package co.com.bancolombia.api.constants;

public final class ApiConstants {

    // --- Logs de Creación de Solicitud ---
    public static final String LOG_REQUEST_RECEIVED = "Recibida petición para crear solicitud del cliente con documento: {}";
    public static final String LOG_REQUEST_CREATED = "Solicitud creada exitosamente con ID: {}";

    // --- Mensajes de Validación DTO de UpdateStatus ---
    public static final String MESSAGE_REQUEST_ID_NULL = "El ID de la solicitud no puede ser nulo.";
    public static final String MESSAGE_REQUEST_STATE_NULL = "El nuevo estado no puede estar vacío.";
    public static final String MESSAGE_REQUEST_RESPONSE_STATE = "El estado solo puede ser 'APPROVED' o 'REJECTED'.";

    // --- Mensajes de Log en el Handler para UpdateStatus ---
    public static final String LOG_HANDLER_VALIDATION_ERROR = "Error de validación en DTO para actualizar estado: {}";
    public static final String LOG_HANDLER_UPDATE_REQUEST_RECEIVED = "Recibida solicitud para actualizar estado de préstamo ID: {} a {}";
    public static final String LOG_HANDLER_BUSINESS_ERROR = "Error de negocio al actualizar estado de préstamo ID {}. Mensaje: {} - Código: {}";
    public static final String LOG_HANDLER_UNEXPECTED_ERROR = "Error inesperado al actualizar estado de préstamo ID {}. Mensaje: {}";

    // --- Mensajes de Error para el Cliente (complementan los códigos) ---
    public static final String ERROR_MESSAGE_VALIDATION = "Error de validación en la solicitud: ";
    public static final String ERROR_MESSAGE_INTERNAL = "Ocurrió un error interno al procesar la solicitud.";

    // --- Códigos de Error (API-specific) ---
    public static final String ERROR_CODE_VALIDATION = "VALID001";
    public static final String ERROR_CODE_INTERNAL = "GEN001";

    // --- Códigos de Éxito (Opcional, pero útil para consistencia si los errores tienen código) ---
    public static final String SUCCESS_CODE_STATUS_UPDATED = "APP-STA001";
    public static final String MESSAGE_STATUS_UPDATED = "Estado de la solicitud actualizado exitosamente.";


    // --- Rutas de API ---
    public static final String LOAN_REQUEST_PATH = "/api/v1/requests";
    public static final String LOAN_APPLICATION_STATUS_PATH = "/api/v1/loan-applications/{id}/status";
    public static final String LOAN_REF_SCHEMA_PATH = "#/components/schemas/";

    // --- CONSTANTES PARA SWAGGER (sin cambios) ---
    public static final String SWAGGER_API_TITLE = "API de Solicitudes de Crédito";
    public static final String SWAGGER_API_VERSION = "1.0";
    public static final String SWAGGER_API_DESCRIPTION = "Microservicio para gestionar solicitudes de crédito.";
    public static final String SWAGGER_SECURITY_SCHEME_NAME = "jwt";
    public static final String SWAGGER_SECURITY_SCHEME_TYPE_HTTP = "bearer";
    public static final String SWAGGER_SECURITY_SCHEME_FORMAT_JWT = "JWT";
    public static final String SWAGGER_SECURITY_SCHEME_HEADER_NAME = "Authorization";
    public static final String SWAGGER_TAG_LOAN_APPLICATION = "Loan Application";

    // POST /api/v1/requests
    public static final String SWAGGER_POST_OPERATION_ID = "createLoanApplication";
    public static final String SWAGGER_POST_SUMMARY = "Crear una nueva solicitud de crédito";
    public static final String SWAGGER_POST_RESP_201 = "Solicitud creada exitosamente";
    public static final String SWAGGER_POST_RESP_400 = "Error de validación o cliente no encontrado";

    // GET /api/v1/requests
    public static final String SWAGGER_GET_OPERATION_ID = "getApplicationsForReview";
    public static final String SWAGGER_GET_SUMMARY = "Obtener lista de solicitudes para revisión";
    public static final String SWAGGER_GET_PARAM_PAGE = "page";
    public static final String SWAGGER_GET_PARAM_PAGE_DESC = "Número de página (inicia en 0)";
    public static final String SWAGGER_GET_PARAM_SIZE = "size";
    public static final String SWAGGER_GET_PARAM_SIZE_DESC = "Tamaño de la página";
    public static final String SWAGGER_GET_RESP_200 = "Lista de solicitudes obtenida exitosamente";
    public static final String SWAGGER_GET_RESP_401 = "No autorizado";
    public static final String SWAGGER_GET_RESP_403 = "Acceso denegado";

    // PUT /api/v1/loan-applications/{id}/status (NUEVO)
    public static final String SWAGGER_PUT_OPERATION_ID = "updateLoanApplicationStatus";
    public static final String SWAGGER_PUT_SUMMARY = "Actualizar el estado de una solicitud de crédito a APROBADO o RECHAZADO";
    public static final String SWAGGER_PUT_PARAM_ID = "id";
    public static final String SWAGGER_PUT_PARAM_ID_DESC = "ID de la solicitud de crédito";
    public static final String SWAGGER_PUT_REQ_BODY_DESC = "Nuevo estado y comentarios para la solicitud de crédito";
    public static final String SWAGGER_PUT_RESP_200 = "Estado de la solicitud actualizado exitosamente";
    public static final String SWAGGER_PUT_RESP_400_INVALID_ID = "ID de solicitud no válido o formato incorrecto";
    public static final String SWAGGER_PUT_RESP_400_BUSINESS = "Error de negocio: solicitud no encontrada, estado inválido o rol no autorizado";
    public static final String SWAGGER_PUT_RESP_401 = "No autorizado";
    public static final String SWAGGER_PUT_RESP_403 = "Acceso denegado (rol no permitido)";
    public static final String SWAGGER_PUT_RESP_500 = "Error interno del servidor";

    // Schemas
    public static final String SWAGGER_SCHEMA_LOAN_APP_DETAIL_DTO = "LoanApplicationDetailDTO";
    public static final String SWAGGER_SCHEMA_LOAN_APP_DTO = "LoanApplicationDTO";
    public static final String SWAGGER_SCHEMA_LOAN_APP = "LoanApplication";
    public static final String SWAGGER_SCHEMA_UPDATE_LOAN_APP_STATUS_REQ_DTO = "UpdateLoanApplicationStatusRequestDTO";

    private ApiConstants() {}
}