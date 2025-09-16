package co.com.bancolombia.drivenadapters.sqs.validation;

import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.validation.ValidationRequestMessage;
import co.com.bancolombia.model.validation.gateways.ValidationQueueService;
import co.com.bancolombia.usecase.constants.LoanUseCaseConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


@Component
@RequiredArgsConstructor
public class SqsValidationQueueAdapter implements ValidationQueueService {
    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;
    private final LoggerService logger;

    @Value("${aws.sqs.validationQueueUrl}")
    private String validationQueueUrl;

    @Override
    public Mono<Void> sendValidationRequest(ValidationRequestMessage message) {
        return Mono.fromCallable(() -> {
                    try {
                        return objectMapper.writeValueAsString(message);
                    } catch (JsonProcessingException e) {
                        logger.error(LoanUseCaseConstants.LOG_SQS_SERIALIZATION_ERROR, e.getMessage(), e);
                        throw new RuntimeException(LoanUseCaseConstants.LOG_SQS_SERIALIZATION_EXCEPTION, e);
                    }
                })
                .flatMap(messageBody -> {
                    SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                            .queueUrl(validationQueueUrl)
                            .messageBody(messageBody)
                            .build();

                    logger.info(LoanUseCaseConstants.LOG_SQS_SENDING_VALIDATION_MESSAGE, message.getLoanApplicationId());
                    return Mono.fromFuture(sqsAsyncClient.sendMessage(sendMessageRequest))
                            .doOnSuccess(response -> logger.info(LoanUseCaseConstants.LOG_SQS_VALIDATION_MESSAGE_SENT_SUCCESS, response.messageId()))
                            .doOnError(e -> logger.error(LoanUseCaseConstants.LOG_SQS_VALIDATION_MESSAGE_SEND_ERROR, e.getMessage(), e))
                            .onErrorResume(e -> Mono.error(new RuntimeException(LoanUseCaseConstants.LOG_SQS_VALIDATION_MESSAGE_SEND_ERROR_FAIL, e)))
                            .then();
                });
    }
}
