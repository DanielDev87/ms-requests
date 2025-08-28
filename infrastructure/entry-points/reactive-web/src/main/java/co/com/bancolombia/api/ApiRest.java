package co.com.bancolombia.api;
import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.model.loanapplication.LoanApplication;

import co.com.bancolombia.usecase.createloanapplication.CreateLoanApplicationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/requests", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ApiRest {

    private final CreateLoanApplicationUseCase createLoanApplicationUseCase;
    private final TransactionalOperator transactionalOperator;

    @PostMapping
    public Mono<ResponseEntity<LoanApplication>> createLoanApplication(@Valid @RequestBody LoanApplicationDTO loanApplicationDTO) {
        log.info("Recibida petición para crear solicitud del cliente ID: {}", loanApplicationDTO.getDocumentNumber());
        return createLoanApplicationUseCase.execute(toModel(loanApplicationDTO))
                .map(savedApplication -> {
                    log.info("Solicitud creada exitosamente con ID: {}", savedApplication.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedApplication);
                })
                .as(transactionalOperator::transactional); // <-- APLICAR TRANSACCIÓN
    }

    private LoanApplication toModel(LoanApplicationDTO dto) {
        return LoanApplication.builder()
                .documentNumber(dto.getDocumentNumber())
                .amount(dto.getAmount())
                .term(dto.getTerm())
                .loanTypeId(dto.getLoanTypeId())
                .build();
    }
}
