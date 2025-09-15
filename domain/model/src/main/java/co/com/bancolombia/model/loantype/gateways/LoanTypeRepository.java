package co.com.bancolombia.model.loantype.gateways;

import co.com.bancolombia.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Mono<Boolean> existsById(Long id);
    Mono<LoanType> findById(Long id);
}
