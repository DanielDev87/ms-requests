package co.com.bancolombia.usecase.getapplicationsforreview;

import co.com.bancolombia.model.loanapplication.LoanApplicationDetail;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.usecase.constants.LoanUseCaseConstants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import java.awt.print.Pageable;

@RequiredArgsConstructor
public class GetApplicationsForReviewUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoggerService logger;

    public Flux<LoanApplicationDetail> execute(int page, int size) {
        logger.info(LoanUseCaseConstants.LOG_INIT_PAGINATED_SEARCH, page, size);

        return loanApplicationRepository.findApplicationsForReview(page, size);
    }
}