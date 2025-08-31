package co.com.bancolombia.r2dbc.mapper;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.r2dbc.data.LoanApplicationData;
import org.springframework.stereotype.Component;

@Component
public class LoanApplicationMapper {

    public LoanApplication toDomain(LoanApplicationData data) {
        return LoanApplication.builder()
                .id(data.getId())
                .clientId(data.getClientId())
                .amount(data.getAmount())
                .term(data.getTerm())
                .loanTypeId(data.getLoanTypeId())
                .status(LoanApplication.Status.valueOf(data.getStatus()))
                .requestDate(data.getRequestDate())
                .build();
    }

    public LoanApplicationData toData(LoanApplication domain) {
        return LoanApplicationData.builder()
                .id(domain.getId())
                .clientId(domain.getClientId())
                .amount(domain.getAmount())
                .term(domain.getTerm())
                .loanTypeId(domain.getLoanTypeId())
                .status(domain.getStatus().name())
                .requestDate(domain.getRequestDate())
                .build();
    }
}
