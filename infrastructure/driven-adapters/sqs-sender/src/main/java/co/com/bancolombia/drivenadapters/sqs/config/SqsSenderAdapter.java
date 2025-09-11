package co.com.bancolombia.drivenadapters.sqs.config;

import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.log.gateways.LoggerService;
import co.com.bancolombia.model.notificationmessage.NotificationMessage;
import co.com.bancolombia.model.notificationmessage.gateways.NotificationService;
import co.com.bancolombia.model.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
public class SqsSenderAdapter implements NotificationService {

    private final SqsAsyncClient sqsClient;
    private final ObjectMapper objectMapper;
    private final LoggerService logger;

    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;

    @Override
    public Mono<Void> sendNotification(NotificationMessage message) {
        return Mono.fromCallable(() -> {
                    try {
                        return objectMapper.writeValueAsString(message);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(Constants.SQS_ERROR_SERIALIZATION, e);
                    }
                })
                .flatMap(messageBody -> {
                    logger.info(Constants.SQS_LOG_SENDING_MESSAGE, message.getLoanApplicationId());
                    SendMessageRequest sendRequest = SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(messageBody)
                            .build();

                    return Mono.fromFuture(sqsClient.sendMessage(sendRequest));
                })
                .doOnSuccess(response -> logger.info(Constants.SQS_LOG_MESSAGE_SENT_SUCCESS, response.messageId()))
                .doOnError(e ->
                        logger.error(Constants.SQS_LOG_ERROR_SENDING_MESSAGE, e, message.getLoanApplicationId(), e.getMessage())
                )
                .then()
                .onErrorMap(e -> {
                    if (e instanceof BusinessException) {
                        return e;
                    }
                    return BusinessException.notificationSendError(Long.valueOf(message.getLoanApplicationId()), e);
                });
    }
}
