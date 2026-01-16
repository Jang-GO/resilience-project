package janggo.practice.callerservice.async.service;

import janggo.practice.callerservice.async.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AsyncNotificationService {

    private final RestClient restClient;

    @Autowired
    public AsyncNotificationService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8080")
                .build();
    }

    /**
     * @Async를 사용한 비동기 알림 발송
     * - 별도 스레드풀에서 실행됨
     * - 메인 트랜잭션과 분리됨
     * - 실패해도 주문 트랜잭션에 영향 없음
     */
    @Async("notificationExecutor")
    public CompletableFuture<Void> sendNotificationAsync(NotificationRequest request) {
        String threadName = Thread.currentThread().getName();
        log.info("=== [ASYNC] 알림 발송 시작 === Thread: {}, OrderId: {}", threadName, request.orderId());

        try {
            // 외부 Notification Service 호출
            String response = restClient.post()
                    .uri("/notifications")
                    .body(request)
                    .retrieve()
                    .body(String.class);

            log.info("=== [ASYNC] 알림 발송 성공 === Thread: {}, Response: {}", threadName, response);
            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("=== [ASYNC] 알림 발송 실패 === Thread: {}, Error: {}", threadName, e.getMessage());
            // 실패해도 예외를 던지지 않음 (주문 트랜잭션에 영향 없음)
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * 동기 방식 알림 발송 (비교용)
     */
    public void sendNotificationSync(NotificationRequest request) {
        String threadName = Thread.currentThread().getName();
        log.info("=== [SYNC] 알림 발송 시작 === Thread: {}, OrderId: {}", threadName, request.orderId());

        try {
            String response = restClient.post()
                    .uri("/notifications")
                    .body(request)
                    .retrieve()
                    .body(String.class);

            log.info("=== [SYNC] 알림 발송 성공 === Thread: {}, Response: {}", threadName, response);

        } catch (Exception e) {
            log.error("=== [SYNC] 알림 발송 실패 === Thread: {}, Error: {}", threadName, e.getMessage());
            throw e;  // 동기 방식은 예외를 전파 (주문 실패 가능)
        }
    }
}
