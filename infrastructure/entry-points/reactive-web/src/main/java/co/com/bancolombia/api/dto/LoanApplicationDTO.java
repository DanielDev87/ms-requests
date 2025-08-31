package co.com.bancolombia.api.dto;

import co.com.bancolombia.api.validation.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(name = "LoanApplicationRequest", description = "DTO para crear una nueva solicitud de crédito")
public class LoanApplicationDTO {

    @NotBlank(message = ValidationConstants.DOCUMENT_NUMBER_NOT_BLANK)
    @Schema(description = "Número de documento del cliente", example = "1037665432", required = true)
    private String documentNumber;

    @NotNull(message = ValidationConstants.AMOUNT_NOT_NULL)
    @DecimalMin(value = "1000000.0", message = ValidationConstants.AMOUNT_MIN_VALUE)
    @Schema(description = "Monto total solicitado", example = "50000000.00", required = true)
    private BigDecimal amount;

    @NotNull(message = ValidationConstants.TERM_NOT_NULL)
    @Min(value = 12, message = ValidationConstants.TERM_MIN_VALUE)
    @Schema(description = "Plazo del crédito en meses", example = "60", required = true)
    private Integer term;

    @NotNull(message = ValidationConstants.LOAN_TYPE_ID_NOT_NULL)
    @Schema(description = "ID del tipo de crédito al que se aplica", example = "3", required = true)
    private Long loanTypeId;
}