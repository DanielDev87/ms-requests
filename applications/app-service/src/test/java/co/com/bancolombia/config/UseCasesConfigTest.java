package co.com.bancolombia.config;

import co.com.bancolombia.model.client.gateways.ClientValidationGateway;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.notificationmessage.gateways.NotificationService;
import co.com.bancolombia.model.security.gateways.SecurityContextGateway;
import co.com.bancolombia.usecase.createloanapplication.CreateLoanApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = UseCasesConfig.class)
class UseCasesConfigTest {
    @Autowired
    private UseCasesConfig useCasesConfig;

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
    @MockBean
    private NotificationService notificationService;

    @Test
    void createLoanApplicationUseCaseBeanShouldBeLoaded() {

        var useCase = useCasesConfig.createLoanApplicationUseCase(
                loanApplicationRepository,
                loanTypeRepository,
                clientValidationGateway,
                loggerService,
                securityContextGateway
        );
        assertThat(useCase).isNotNull();
    }

    @Test
    void updateLoanApplicationStatusUseCaseBeanShouldBeLoaded() {
        var useCase = useCasesConfig.updateLoanApplicationStatusUseCase(
                loanApplicationRepository,
                notificationService, // Pasar el mock
                securityContextGateway,
                loggerService
        );
        assertThat(useCase).isNotNull();
    }
}