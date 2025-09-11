package co.com.bancolombia.api;

import co.com.bancolombia.api.constants.ApiConstants;
import co.com.bancolombia.api.handler.Handler;

import co.com.bancolombia.api.handler.LoanApplicationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(
            Handler handler,
            LoanApplicationHandler loanApplicationHandler
    ) {
        return route(POST(ApiConstants.LOAN_REQUEST_PATH), handler::createLoanApplication)
                .andRoute(GET(ApiConstants.LOAN_REQUEST_PATH), handler::getApplicationsForReview)
                .andRoute(PUT(ApiConstants.LOAN_APPLICATION_STATUS_PATH), loanApplicationHandler::updateStatus);
    }

}
