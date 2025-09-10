package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.LoanApplicationDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
    Flux<LoanApplicationDetail> findApplicationsForReview(int page, int size);
    Mono<LoanApplication> findById(Long id);
    //Flux<LoanApplication> findByStatus(LoanApplication.Status status);
}
