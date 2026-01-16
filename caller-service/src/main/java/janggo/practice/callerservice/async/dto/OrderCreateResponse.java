package janggo.practice.callerservice.async.dto;

import java.time.LocalDateTime;

public record OrderCreateResponse(
        String orderId,
        String customerId,
        String status,
        LocalDateTime createdAt,
        String message
) {
}
