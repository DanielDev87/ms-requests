package co.com.bancolombia.model.notificationmessage.gateways;

import co.com.bancolombia.model.notificationmessage.NotificationMessage;
import reactor.core.publisher.Mono;

public interface NotificationService {
    Mono<Void> sendNotification(NotificationMessage message);

}
