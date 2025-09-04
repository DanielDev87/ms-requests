package co.com.bancolombia.model.client;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Long id;
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal baseSalary;
}
