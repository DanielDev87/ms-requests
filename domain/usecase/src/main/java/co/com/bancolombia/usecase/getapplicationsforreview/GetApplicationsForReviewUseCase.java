package co.com.bancolombia.usecase.getapplicationsforreview;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
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
    private final ClientValidationGateway clientValidationGateway;
    private final LoggerService logger;

    public Flux<LoanApplicationDetail> execute(int page, int size) {
        logger.info(LoanUseCaseConstants.LOG_INIT_PAGINATED_SEARCH, page, size);

        return loanApplicationRepository.findApplicationsForReview(page, size)
                .flatMap(this::enrichWithClientData);
    }

    private Flux<LoanApplicationDetail> enrichWithClientData(LoanApplicationDetail application) {
        return clientValidationGateway.findClientByDocumentNumber(application.getDocumentNumber())
                .flatMapMany(clientData -> {
                    LoanApplicationDetail completeDetail = application.toBuilder()
                            .clientEmail(clientData.getEmail())
                            .clientFullName(clientData.getFirstName() + " " + clientData.getLastName())
                            .clientBaseSalary(clientData.getBaseSalary())
                            // Aún no tenemos esta información, la dejamos para el final
                            // .clientMonthlyDebt(...)
                            .build();
                    return Flux.just(completeDetail);
                })
                .switchIfEmpty(Flux.empty());
    }
}