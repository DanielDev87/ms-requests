package co.com.bancolombia.usecase.constants;

public class LoanUseCaseConstants {
    // --- Logs de CreateLoanApplicationUseCase ---
    public static final String LOG_INIT_CREATE_APP = "Iniciando creación de solicitud para cliente con documento: {}";
    public static final String LOG_CLIENT_FOUND = "Cliente encontrado con ID: {}. Asignando a la solicitud.";
    public static final String LOG_LOAN_TYPE_INVALID = "El tipo de préstamo {} no existe.";
    public static final String LOG_SAVING_APP = "Validaciones completadas. Guardando solicitud...";
    public static final String LOG_WARN_UNAUTHORIZED_OPERATION = "Intento de operación no autorizada: El token ({}) no corresponde al documento de la solicitud ({}).";

    // --- VALIDACIÓN AUTOMÁTICA
    public static final String LOG_AUTO_VALIDATION_REQUIRED = "Solicitud {} (Tipo {} = {}) requiere validación automática. Encolando mensaje.";
    public static final String LOG_AUTO_VALIDATION_NOT_REQUIRED = "Solicitud {} (Tipo {} = {}) no requiere validación automática. Finalizando.";
    public static final String LOG_ERROR_ENQUEUING_VALIDATION_MESSAGE = "Error al encolar mensaje de validación para solicitud {}: {}";


    // --- Logs de UpdateLoanApplicationStatusUseCase ---
    public static final String LOG_UPDATE_STATUS_INIT = "Inicio de actualización de estado para solicitud ID: {}, nuevo estado: {}";
    public static final String LOG_UNAUTHORIZED_ROLE = "Usuario con rol '{}' intentó actualizar solicitud {}. No autorizado.";
    public static final String LOG_INVALID_STATUS_VALUE = "Estado '{}' inválido para solicitud {}.";
    public static final String LOG_STATUS_NOT_ALLOWED = "Intento de cambiar solicitud {} a estado no permitido por este caso de uso: {}.";
    public static final String LOG_STATUS_MUST_BE_PENDING = "Solicitud {} no puede cambiar de estado de {} a {}. Debe estar en PENDING.";
    public static final String LOG_STATUS_UPDATED_NOTIFICATION = "Estado de solicitud {} actualizado a {}. Enviando notificación...";
    public static final String LOG_NOTIFICATION_SEND_ERROR = "Error al enviar notificación para solicitud {}: {}";

    // --- Logs del Adaptador SQS ---
    public static final String LOG_SQS_SERIALIZATION_ERROR = "Error al serializar el mensaje de validación: {}";
    public static final String LOG_SQS_SERIALIZATION_EXCEPTION = "Error al serializar el mensaje de validación";
    public static final String LOG_SQS_SENDING_VALIDATION_MESSAGE = "Enviando mensaje de validación a SQS para solicitud {}.";
    public static final String LOG_SQS_VALIDATION_MESSAGE_SENT_SUCCESS = "Mensaje de validación enviado a SQS con éxito. MessageId: {}";
    public static final String LOG_SQS_VALIDATION_MESSAGE_SEND_ERROR = "Error al enviar mensaje de validación a SQS: {}";
    public static final String LOG_SQS_VALIDATION_MESSAGE_SEND_ERROR_FAIL = "Fallo al enviar mensaje SQS de validación";

    // --- Errores de Negocio ---
    public static final String ERROR_CLIENT_NOT_FOUND = "El cliente con el documento especificado no existe.";
    public static final String ERROR_LOAN_TYPE_NOT_FOUND = "El tipo de préstamo especificado no existe.";
    public static final String ERROR_UNAUTHORIZED_CLIENT_OPERATION_FORMAT = "Document number from token (%s) does not match document number in request (%s). Unauthorized operation.";


    // --- Búsqueda ---
    public static final String LOG_INIT_PAGINATED_SEARCH = "Iniciando búsqueda paginada de solicitudes para revisión. Página: {}, Tamaño: {}";

    private LoanUseCaseConstants() {
    }
}