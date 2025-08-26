package co.com.bancolombia.r2dbc.data;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@Table("loan_applications")
public class LoanApplicationData {

    @Id
    private Long id;

    @Column("client_id")
    private Long clientId;

    private BigDecimal amount;
    private Integer term;

    @Column("loan_type_id")
    private Long loanTypeId;

    private String status;

    @Column("request_date")
    private LocalDate requestDate;
}