package co.com.bancolombia.usecase.getapplicationsforreview;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.loanapplication.LoanApplicationDetail;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.usecase.constants.LoanUseCaseConstants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    private Mono<LoanApplicationDetail> enrichWithClientData(LoanApplicationDetail application) {
        return clientValidationGateway.findClientByDocumentNumber(application.getDocumentNumber())
                .map(client -> application.toBuilder()
                        .clientEmail(client.getEmail())
                        .clientFullName(client.getFirstName() + " " + client.getLastName())
                        .clientBaseSalary(client.getBaseSalary())
                        .build())
                // Si el Mono está vacío (cliente no encontrado), devuelve el objeto original
                .defaultIfEmpty(application);
    }
}