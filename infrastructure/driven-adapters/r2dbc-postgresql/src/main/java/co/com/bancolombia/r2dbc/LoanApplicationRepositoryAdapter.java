package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.LoanApplicationDetail;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.config.DBQueries;
import co.com.bancolombia.r2dbc.data.LoanApplicationData;
import co.com.bancolombia.r2dbc.data.LoanApplicationDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LoanApplicationRepositoryAdapter implements LoanApplicationRepository {

    private final DatabaseClient databaseClient;
    private final LoanApplicationDataRepository repository;

    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        return Mono.just(loanApplication)
                .map(this::toData)
                .flatMap(repository::save)
                .map(this::toDomain);
    }

    @Override
    public Flux<LoanApplicationDetail> findApplicationsForReview(int page, int size) {
        List<String> statuses = List.of("PENDING", "REJECTED", "MANUAL_REVIEW");
        int offset = page * size;


        return databaseClient.sql(DBQueries.FIND_APPLICATIONS_FOR_REVIEW)
                .bind("statuses", statuses)
                .bind("size", size)
                .bind("offset", offset)
                .map((row, metadata) -> LoanApplicationDetail.builder()
                        // Mapeamos solo los campos que obtenemos de la BD
                        .documentNumber(row.get("document_number", String.class))
                        .amount(row.get("amount", Double.class))
                        .term(row.get("term", Integer.class))
                        .status(row.get("status", String.class))
                        .loanTypeName(row.get("loan_type_name", String.class))
                        .interestRate(row.get("interest_rate", Double.class))
                        .build())
                .all();
    }

    private LoanApplication toDomain(LoanApplicationData data) {
        return LoanApplication.builder()
                .id(data.getId())
                .clientId(data.getClientId())
                .documentNumber(data.getDocumentNumber())
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
                .documentNumber(domain.getDocumentNumber())
                .amount(domain.getAmount())
                .term(domain.getTerm())
                .loanTypeId(domain.getLoanTypeId())
                .status(domain.getStatus().name())
                .requestDate(domain.getRequestDate())
                .build();
    }
}
