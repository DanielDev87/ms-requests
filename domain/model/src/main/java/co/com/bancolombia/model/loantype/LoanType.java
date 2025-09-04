package co.com.bancolombia.model.loantype;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class LoanType {
    private Long id;
    private String name;
    private BigDecimal interestRate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}
