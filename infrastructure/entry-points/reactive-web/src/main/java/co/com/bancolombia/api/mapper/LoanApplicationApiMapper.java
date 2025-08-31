package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import org.springframework.stereotype.Component;

@Component
public class LoanApplicationApiMapper {

    public LoanApplication toModel(LoanApplicationDTO dto) {
        return LoanApplication.builder()
                .documentNumber(dto.getDocumentNumber())
                .amount(dto.getAmount())
                .term(dto.getTerm())
                .loanTypeId(dto.getLoanTypeId())
                .build();
    }
}
