package janggo.practice.calleeservice.notification.controller;

import janggo.practice.calleeservice.notification.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final Map<String, Integer> orderNotificationCount = new ConcurrentHashMap<>();
    private volatile boolean shouldFail = false;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendNotification(
            @RequestBody NotificationRequest request) {

        int count = requestCount.incrementAndGet();
        String threadName = Thread.currentThread().getName();

        log.info("=== [NOTIFICATION] 알림 요청 수신 === Thread: {}, Count: {}, OrderId: {}",
                threadName, count, request.orderId());

        orderNotificationCount.merge(request.orderId(), 1, Integer::sum);

        if (shouldFail) {
            log.error("=== [NOTIFICATION] 실패 (장애 상태) === OrderId: {}", request.orderId());
            return ResponseEntity.status(503).body(Map.of(
                    "success", false,
                    "message", "Service unavailable"
            ));
        }

        try {
            // 2초 처리 시뮬레이션
            Thread.sleep(2000);

            log.info("=== [NOTIFICATION] 발송 완료 === Type: {}, CustomerId: {}",
                    request.type(), request.customerId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification sent",
                    "orderId", request.orderId(),
                    "processedBy", threadName
            ));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body(Map.of("success", false));
        }
    }

    @PostMapping("/fail")
    public ResponseEntity<String> setFail() {
        shouldFail = true;
        log.warn("### 장애 상태 활성화 ###");
        return ResponseEntity.ok("FAIL mode");
    }

    @PostMapping("/recover")
    public ResponseEntity<String> setRecover() {
        shouldFail = false;
        log.info("### 정상 상태 복구 ###");
        return ResponseEntity.ok("NORMAL mode");
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
                "totalRequests", requestCount.get(),
                "status", shouldFail ? "FAIL" : "NORMAL",
                "orderCounts", orderNotificationCount
        ));
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        requestCount.set(0);
        orderNotificationCount.clear();
        shouldFail = false;
        return ResponseEntity.ok("Reset complete");
    }
}
