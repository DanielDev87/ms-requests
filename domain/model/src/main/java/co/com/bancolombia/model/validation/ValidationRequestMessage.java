package co.com.bancolombia.model.validation;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class ValidationRequestMessage {
    private Long loanApplicationId;
    private Long clientId;
    private String documentNumber;
    private BigDecimal amount;
    private Integer term;
    private BigDecimal interestRate;
}
