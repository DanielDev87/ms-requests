package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanTypeR2dbcRepository extends ReactiveCrudRepository<LoanTypeEntity, Long> {
}
