package janggo.practice.callerservice.async.service;

import janggo.practice.callerservice.async.dto.NotificationRequest;
import janggo.practice.callerservice.async.dto.OrderCreateRequest;
import janggo.practice.callerservice.async.dto.OrderCreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final AsyncNotificationService notificationService;

    /**
     * 주문 생성 (비동기 알림)
     */
    public OrderCreateResponse createOrderWithAsyncNotification(OrderCreateRequest request) {
        log.info("=== 주문 생성 시작 (비동기 알림) === CustomerId: {}, ProductId: {}",
                request.customerId(), request.productId());

        // 1. 주문 생성 (DB 저장 시뮬레이션)
        String orderId = UUID.randomUUID().toString();
        log.info("주문 생성 완료: OrderId={}", orderId);

        // 2. 비동기로 알림 발송 (별도 스레드에서 실행)
        NotificationRequest notification = new NotificationRequest(
                request.customerId(),
                orderId,
                String.format("주문이 완료되었습니다. 주문번호: %s, 상품: %s", orderId, request.productId()),
                "EMAIL"
        );

        notificationService.sendNotificationAsync(notification);
        log.info("알림 발송 요청 완료 (비동기 처리 중)");

        // 3. 즉시 응답 반환 (알림 완료를 기다리지 않음)
        return new OrderCreateResponse(
                orderId,
                request.customerId(),
                "COMPLETED",
                LocalDateTime.now(),
                "주문이 정상적으로 처리되었습니다. 알림은 비동기로 발송됩니다."
        );
    }

    /**
     * 주문 생성 (동기 알림 - 비교용)
     */
    public OrderCreateResponse createOrderWithSyncNotification(OrderCreateRequest request) {
        log.info("=== 주문 생성 시작 (동기 알림) === CustomerId: {}, ProductId: {}",
                request.customerId(), request.productId());

        // 1. 주문 생성
        String orderId = UUID.randomUUID().toString();
        log.info("주문 생성 완료: OrderId={}", orderId);

        // 2. 동기로 알림 발송 (같은 스레드에서 실행, 완료될 때까지 대기)
        NotificationRequest notification = new NotificationRequest(
                request.customerId(),
                orderId,
                String.format("주문이 완료되었습니다. 주문번호: %s, 상품: %s", orderId, request.productId()),
                "EMAIL"
        );

        try {
            notificationService.sendNotificationSync(notification);
            log.info("알림 발송 완료 (동기)");
        } catch (Exception e) {
            log.error("알림 발송 실패 - 주문을 롤백합니다", e);
            throw new RuntimeException("알림 발송 실패로 주문이 취소되었습니다", e);
        }

        // 3. 알림 완료 후 응답 반환
        return new OrderCreateResponse(
                orderId,
                request.customerId(),
                "COMPLETED",
                LocalDateTime.now(),
                "주문 및 알림 발송이 완료되었습니다."
        );
    }
}
