package janggo.practice.callerservice.async.dto;

public record OrderCreateRequest(
        String customerId,
        String productId,
        int quantity,
        double totalAmount
) {
}
