package co.com.bancolombia.model.notificationmessage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class NotificationMessage {
    private String loanApplicationId;
    private String newStatus;
    private String applicantEmail;
    private String applicantName;
    private String comments;
    private String documentNumber;
}
