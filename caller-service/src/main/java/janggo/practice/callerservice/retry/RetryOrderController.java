package janggo.practice.callerservice.retry;

import janggo.practice.callerservice.retry.dto.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RetryOrderController {

    private static final Logger log = LoggerFactory.getLogger(RetryOrderController.class);
    private final ProductApiClient productApiClient;

    public RetryOrderController(ProductApiClient productApiClient) {
        this.productApiClient = productApiClient;
    }

    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        log.info("주문 요청 수신: {}", request.productId());

        try {
            boolean hasStock = productApiClient.checkStock(request.productId());

            if (hasStock) {
                log.info("재고 확인 성공. 주문을 생성합니다.");
                return ResponseEntity.ok("주문 성공!");
            } else {
                log.warn("재고 부족 또는 재고 확인 실패로 주문이 실패했습니다.");
                return ResponseEntity.status(400).body("재고 부족으로 주문 실패");
            }
        } catch (Exception e) {
            log.error("주문 처리 중 예외 발생", e);
            return ResponseEntity.status(500).body("서버 오류로 주문 실패");
        }
    }
}
