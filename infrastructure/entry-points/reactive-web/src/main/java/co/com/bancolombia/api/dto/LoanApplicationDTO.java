package co.com.bancolombia.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanApplicationDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo")
    @Positive(message = "El ID del cliente debe ser un número positivo")
    private Long clientId;

    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser un valor positivo")
    private BigDecimal amount;

    @NotNull(message = "El plazo no puede ser nulo")
    @Positive(message = "El plazo debe ser un valor positivo")
    private Integer term;

    @NotNull(message = "El tipo de préstamo no puede ser nulo")
    private Long loanTypeId;
}