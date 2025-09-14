package co.com.bancolombia.model.loanapplication;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LoanApplication {

    private Long id;
    private Long clientId;
    private Long loanTypeId;
    private String documentNumber;
    private BigDecimal amount;
    private Integer term;
    private Status status;
    private LocalDate requestDate;

    public enum Status {
        PENDING, // Pendiente de revisión
        APPROVED, // Aprobado
        REJECTED  // Rechazado
    }
}