package co.com.bancolombia.r2dbc.data;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanApplicationDataRepository extends R2dbcRepository<LoanApplicationData, Long> {
}