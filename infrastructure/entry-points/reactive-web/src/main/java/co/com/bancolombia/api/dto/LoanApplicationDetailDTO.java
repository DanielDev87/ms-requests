package co.com.bancolombia.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class LoanApplicationDetailDTO {
    private Double amount;
    private Integer term;
    private String clientEmail;
    private String clientFullName;
    private String loanTypeName;
    private Double interestRate;
    private String status;
    private BigDecimal clientBaseSalary;
    private BigDecimal clientMonthlyDebt;
}
