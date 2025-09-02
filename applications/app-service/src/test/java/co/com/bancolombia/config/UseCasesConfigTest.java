package co.com.bancolombia.config;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.security.gateways.SecurityContextGateway;
import co.com.bancolombia.usecase.createloanapplication.CreateLoanApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = UseCasesConfig.class)
class UseCasesConfigTest {

    @Autowired
    private CreateLoanApplicationUseCase createLoanApplicationUseCase;

    @MockBean
    private LoanApplicationRepository loanApplicationRepository;
    @MockBean
    private LoanTypeRepository loanTypeRepository;
    @MockBean
    private ClientValidationGateway clientValidationGateway;
    @MockBean
    private LoggerService loggerService;
    @MockBean
    private SecurityContextGateway securityContextGateway;

    @Test
    void createLoanApplicationUseCaseBeanShouldBeLoaded() {

        assertNotNull(createLoanApplicationUseCase, "El bean del caso de uso no debería ser nulo");
    }
}