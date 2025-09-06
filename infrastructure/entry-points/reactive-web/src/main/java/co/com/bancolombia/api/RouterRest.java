package co.com.bancolombia.api;

import co.com.bancolombia.api.constants.ApiConstants;
import co.com.bancolombia.api.handler.Handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> loanApplicationRouter(Handler handler) {
        return route(POST(ApiConstants.LOAN_REQUEST_PATH), handler::createLoanApplication)
                .andRoute(GET(ApiConstants.LOAN_REQUEST_PATH), handler::getApplicationsForReview);
    }

}
