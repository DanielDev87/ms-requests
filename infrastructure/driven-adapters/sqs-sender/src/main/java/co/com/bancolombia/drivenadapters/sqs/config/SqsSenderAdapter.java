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
        return Mono.defer(() -> {
            String messageBody;
            try {
                messageBody = objectMapper.writeValueAsString(message);
            } catch (JsonProcessingException e) {
                logger.error(Constants.SQS_LOG_ERROR_SERIALIZING, e, message.getLoanApplicationId(), e.getMessage());
                throw new BusinessException(
                        String.format("%s para solicitud %s", Constants.SQS_ERROR_SERIALIZATION, message.getLoanApplicationId()),
                        Constants.NOTIFICATION_SEND_ERROR_CODE, e);
            }

            logger.info(Constants.SQS_LOG_SENDING_MESSAGE, message.getLoanApplicationId());
            SendMessageRequest sendRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            return Mono.fromFuture(sqsClient.sendMessage(sendRequest))
                    .doOnSuccess(response -> logger.info(Constants.SQS_LOG_MESSAGE_SENT_SUCCESS, response.messageId()))
                    .doOnError(e -> {
                        logger.error(Constants.SQS_LOG_ERROR_SENDING_MESSAGE, (JsonProcessingException) e, message.getLoanApplicationId(), e.getMessage());
                    })
                    .then()
                    .onErrorMap(e -> {
                        if (e instanceof BusinessException) {
                            return e;
                        }
                        return new BusinessException(
                                String.format("%s para solicitud %s", Constants.SQS_ERROR_GENERAL_SEND, message.getLoanApplicationId()),
                                Constants.NOTIFICATION_SEND_ERROR_CODE, e);
                    });
        });
    }
}
