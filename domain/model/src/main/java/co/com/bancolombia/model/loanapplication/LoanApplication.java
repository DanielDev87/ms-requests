package co.com.bancolombia.model.loanapplication;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {

    private Long id;
    private Long clientId;
    private BigDecimal amount;
    private Integer term;
    private Long loanTypeId;
    private Status status;
    private LocalDate requestDate;

    public enum Status {
        PENDING, // Pendiente de revisión
        APPROVED, // Aprobado
        REJECTED  // Rechazado
    }
}