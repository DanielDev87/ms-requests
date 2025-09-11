package co.com.bancolombia.api.dto;

import co.com.bancolombia.api.constants.ApiConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoanApplicationStatusRequestDTO {

    @NotNull(message = ApiConstants.MESSAGE_REQUEST_ID_NULL)
    private Long id;

    @NotBlank(message = ApiConstants.MESSAGE_REQUEST_STATE_NULL)
    @Pattern(regexp = "APPROVED|REJECTED", message = ApiConstants.MESSAGE_REQUEST_RESPONSE_STATE)
    private String newStatus;

    private String comments;
}