package co.com.bancolombia.usecase.constants;

public class LoanUseCaseConstants {
    // --- Logs de CreateLoanApplicationUseCase ---
    public static final String LOG_INIT_CREATE_APP = "Iniciando creación de solicitud para cliente con documento: {}";
    public static final String LOG_CLIENT_FOUND = "Cliente encontrado con ID: {}. Asignando a la solicitud.";
    public static final String LOG_LOAN_TYPE_INVALID = "El tipo de préstamo {} no existe.";
    public static final String LOG_SAVING_APP = "Validaciones completadas. Guardando solicitud...";
    public static final String LOG_WARN_UNAUTHORIZED_OPERATION = "Intento de operación no autorizada: El token ({}) no corresponde al documento de la solicitud ({}).";


    // --- Logs de UpdateLoanApplicationStatusUseCase ---
    public static final String LOG_UPDATE_STATUS_INIT = "Inicio de actualización de estado para solicitud ID: {}, nuevo estado: {}";
    public static final String LOG_UNAUTHORIZED_ROLE = "Usuario con rol '{}' intentó actualizar solicitud {}. No autorizado.";
    public static final String LOG_INVALID_STATUS_VALUE = "Estado '{}' inválido para solicitud {}.";
    public static final String LOG_STATUS_NOT_ALLOWED = "Intento de cambiar solicitud {} a estado no permitido por este caso de uso: {}.";
    public static final String LOG_STATUS_MUST_BE_PENDING = "Solicitud {} no puede cambiar de estado de {} a {}. Debe estar en PENDING.";
    public static final String LOG_STATUS_UPDATED_NOTIFICATION = "Estado de solicitud {} actualizado a {}. Enviando notificación...";
    public static final String LOG_NOTIFICATION_SEND_ERROR = "Error al enviar notificación para solicitud {}: {}";


    // --- Errores de Negocio ---
    public static final String ERROR_CLIENT_NOT_FOUND = "El cliente con el documento especificado no existe.";
    public static final String ERROR_LOAN_TYPE_NOT_FOUND = "El tipo de préstamo especificado no existe.";
    public static final String ERROR_UNAUTHORIZED_CLIENT_OPERATION_FORMAT = "Document number from token (%s) does not match document number in request (%s). Unauthorized operation.";


    // --- Búsqueda ---
    public static final String LOG_INIT_PAGINATED_SEARCH = "Iniciando búsqueda paginada de solicitudes para revisión. Página: {}, Tamaño: {}";

    private LoanUseCaseConstants() {
    }
}