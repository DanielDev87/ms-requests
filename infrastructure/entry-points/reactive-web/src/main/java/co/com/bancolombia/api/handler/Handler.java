package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.constants.ApiConstants;
import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.api.mapper.LoanApplicationApiMapper;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.usecase.createloanapplication.CreateLoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class Handler {

    private final CreateLoanApplicationUseCase createLoanApplicationUseCase;
    private final TransactionalOperator transactionalOperator;
    private final LoanApplicationApiMapper mapper;
    private final LoggerService logger;

    public Mono<ServerResponse> createLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationDTO.class)
                .doOnNext(dto -> logger.info(ApiConstants.LOG_REQUEST_RECEIVED, dto.getDocumentNumber()))
                .map(mapper::toModel)
                .flatMap(createLoanApplicationUseCase::execute)
                .flatMap(savedApplication -> {
                    logger.info(ApiConstants.LOG_REQUEST_CREATED, savedApplication.getId());
                    return ServerResponse
                            .created(URI.create("/api/v1/requests/" + savedApplication.getId()))
                            .bodyValue(savedApplication);
                })
                .as(transactionalOperator::transactional);
    }
}
