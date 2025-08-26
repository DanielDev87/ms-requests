package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeRepositoryAdapter implements LoanTypeRepository {

    @Override
    public Mono<Boolean> existsById(Long id) {
        // Implementación temporal hasta que tengamos la tabla de tipos de préstamo
        // Asumimos que cualquier ID > 0 es válido
        return Mono.just(id != null && id > 0);
    }
}
