package janggo.practice.calleeservice.notification.dto;

public record NotificationRequest(
        String customerId,
        String orderId,
        String message,
        String type
) {
}
