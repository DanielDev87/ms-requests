package co.com.bancolombia.config;

import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.usecase.createloanapplication.CreateLoanApplicationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.bancolombia.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {
    @Bean
    public CreateLoanApplicationUseCase createLoanApplicationUseCase(
            LoanApplicationRepository loanApplicationRepository,
            LoanTypeRepository loanTypeRepository,
            LoggerService loggerService) {

        return new CreateLoanApplicationUseCase(loanApplicationRepository, loanTypeRepository, loggerService);
    }
}
