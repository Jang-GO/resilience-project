package janggo.practice.calleeservice.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class ProductController {
    private final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final AtomicInteger requestCount = new AtomicInteger(0);

    @GetMapping("/products/{productId}/stock")
    public ResponseEntity<Map<String, Boolean>> getStock(@PathVariable("productId") String productId) {
        log.info("상품 서비스 호출됨. productId: {}, 요청 횟수: {}", productId, requestCount.get() + 1);

        // 검증 오류라고 가정
        if ("INVALID".equals(productId)) {
            log.warn("잘못된 상품 ID '{}'에 대한 요청. 404 Not Found 반환.", productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        int currentCount = requestCount.incrementAndGet();

        // 첫 2번의 요청은 실패(503)
        if (currentCount <= 2) {
            log.warn("서버 과부하 시뮬레이션. 503 Service Unavailable 반환.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        // 3번째 요청부터 성공
        log.info("정상 응답. 재고 있음(true) 반환.");
        return ResponseEntity.ok(Map.of("hasStock", true));
    }
}
