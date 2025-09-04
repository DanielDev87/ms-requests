package co.com.bancolombia.model.loanapplication;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class LoanApplicationDetail {
    // Datos de la solicitud
    private String documentNumber;
    private Double amount;
    private Integer term;
    private String status;

    // Datos del cliente
    private String clientEmail;
    private String clientFullName;
    private BigDecimal clientBaseSalary;
    private BigDecimal clientMonthlyDebt;

    // Datos del tipo de préstamo
    private String loanTypeName;
    private Double interestRate;
}