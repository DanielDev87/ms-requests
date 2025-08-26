package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.data.LoanApplicationData;
import co.com.bancolombia.r2dbc.data.LoanApplicationDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class LoanApplicationRepositoryAdapter implements LoanApplicationRepository {

    private final LoanApplicationDataRepository repository;

    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        return repository.save(toData(loanApplication))
                .map(this::toDomain);
    }

    private LoanApplication toDomain(LoanApplicationData data) {
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

    private LoanApplicationData toData(LoanApplication domain) {
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
