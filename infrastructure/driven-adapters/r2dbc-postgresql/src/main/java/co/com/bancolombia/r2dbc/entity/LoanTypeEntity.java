package co.com.bancolombia.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("loan_types")
public class LoanTypeEntity {
    @Id
    private Long id;
    private String name;
    @Column("interest_rate")
    private BigDecimal interestRate;
    @Column("min_amount")
    private BigDecimal minAmount;
    @Column("max_amount")
    private BigDecimal maxAmount;
    @Column("automatic_validation")
    private boolean automaticValidation;
}
