package co.com.bancolombia.usecase.constants;

public class LoanUseCaseConstants {
    // --- Logs ---
    public static final String LOG_INIT_CREATE_APP = "Iniciando creación de solicitud para cliente con documento: {}";
    public static final String LOG_CLIENT_FOUND = "Cliente encontrado con ID: {}. Asignando a la solicitud.";
    public static final String LOG_LOAN_TYPE_INVALID = "El tipo de préstamo {} no existe.";
    public static final String LOG_SAVING_APP = "Validaciones completadas. Guardando solicitud...";

    // --- Errores de Negocio ---
    public static final String ERROR_CLIENT_NOT_FOUND = "El cliente con el documento especificado no existe.";
    public static final String ERROR_LOAN_TYPE_NOT_FOUND = "El tipo de préstamo especificado no existe.";

    public static final String ERROR_UNAUTHORIZED_CLIENT_OPERATION = "No tiene permisos para crear una solicitud para otro cliente.";
    public static final String LOG_WARN_UNAUTHORIZED_OPERATION = "Intento de operación no autorizada: El token ({}) no corresponde al documento de la solicitud ({}).";

    private LoanUseCaseConstants() {
    }
}
