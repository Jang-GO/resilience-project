package janggo.practice.callerservice.async.controller;

import janggo.practice.callerservice.async.dto.OrderCreateRequest;
import janggo.practice.callerservice.async.dto.OrderCreateResponse;
import janggo.practice.callerservice.async.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/async")
@RequiredArgsConstructor
public class AsyncOrderController {

    private final OrderService orderService;

    /**
     * 비동기 알림 방식 주문 생성
     * POST /async/orders
     *
     * 특징:
     * - 주문 생성 후 즉시 응답 반환
     * - 알림은 별도 스레드에서 비동기 처리
     * - 알림 실패가 주문에 영향 없음
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderCreateResponse> createOrderAsync(
            @RequestBody OrderCreateRequest request) {

        log.info("### [비동기 주문] 요청 수신 ###");
        long startTime = System.currentTimeMillis();

        OrderCreateResponse response = orderService.createOrderWithAsyncNotification(request);

        long duration = System.currentTimeMillis() - startTime;
        log.info("### [비동기 주문] 응답 반환 ### 처리시간: {}ms", duration);

        return ResponseEntity.ok(response);
    }

    /**
     * 동기 알림 방식 주문 생성 (비교용)
     * POST /async/orders/sync
     *
     * 특징:
     * - 주문 생성 후 알림 완료까지 대기
     * - 알림 실패 시 주문도 실패
     * - 응답 시간이 느림
     */
    @PostMapping("/orders/sync")
    public ResponseEntity<OrderCreateResponse> createOrderSync(
            @RequestBody OrderCreateRequest request) {

        log.info("### [동기 주문] 요청 수신 ###");
        long startTime = System.currentTimeMillis();

        try {
            OrderCreateResponse response = orderService.createOrderWithSyncNotification(request);

            long duration = System.currentTimeMillis() - startTime;
            log.info("### [동기 주문] 응답 반환 ### 처리시간: {}ms", duration);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("### [동기 주문] 실패 ### 처리시간: {}ms", duration, e);

            return ResponseEntity.internalServerError().body(
                    new OrderCreateResponse(
                            null,
                            request.customerId(),
                            "FAILED",
                            null,
                            "주문 처리 실패: " + e.getMessage()
                    )
            );
        }
    }
}
