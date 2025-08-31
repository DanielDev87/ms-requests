package co.com.bancolombia.api.validation;

public final class ValidationConstants {

    // --- Mensajes Generales ---
    private static final String NOT_BLANK_MESSAGE = " no puede estar vacío";
    private static final String NOT_NULL_MESSAGE = " no puede ser nulo";

    // --- LoanApplicationDTO ---
    public static final String DOCUMENT_NUMBER_NOT_BLANK = "El número de documento" + NOT_BLANK_MESSAGE;
    public static final String AMOUNT_NOT_NULL = "El monto" + NOT_NULL_MESSAGE;
    public static final String AMOUNT_MIN_VALUE = "El monto mínimo del crédito debe ser de 1,000,000";
    public static final String TERM_NOT_NULL = "El plazo" + NOT_NULL_MESSAGE;
    public static final String TERM_MIN_VALUE = "El plazo mínimo del crédito es de 12 meses";
    public static final String LOAN_TYPE_ID_NOT_NULL = "El tipo de préstamo" + NOT_NULL_MESSAGE;

    private ValidationConstants() {
    }
}
