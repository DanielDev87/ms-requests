package co.com.bancolombia.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClientData {

    private Long id;
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal baseSalary;
}
