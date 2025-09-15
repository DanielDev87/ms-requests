package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.r2dbc.entity.LoanTypeEntity;
import co.com.bancolombia.r2dbc.LoanTypeR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class LoanTypeRepositoryAdapter implements LoanTypeRepository {
    private final LoanTypeR2dbcRepository repository;

    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Mono<LoanType> findById(Long id) {
        return repository.findById(id)
                .map(this::toDomain);
    }


    private LoanType toDomain(LoanTypeEntity entity) {
        return LoanType.builder()
                .id(entity.getId())
                .name(entity.getName())
                .interestRate(entity.getInterestRate())
                .minAmount(entity.getMinAmount())
                .maxAmount(entity.getMaxAmount())
                .automaticValidation(entity.isAutomaticValidation())
                .build();
    }


    private LoanTypeEntity toEntity(LoanType domain) {
        return LoanTypeEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .interestRate(domain.getInterestRate())
                .minAmount(domain.getMinAmount())
                .maxAmount(domain.getMaxAmount())
                .automaticValidation(domain.isAutomaticValidation())
                .build();
    }

}
