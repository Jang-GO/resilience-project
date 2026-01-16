package janggo.practice.callerservice.async.dto;

public record NotificationRequest(
        String customerId,
        String orderId,
        String message,
        String type  // EMAIL, SMS, PUSH
) {
}
