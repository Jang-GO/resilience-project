package janggo.practice.calleeservice.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class ProductControllerCircuit {
    private static final Logger log = LoggerFactory.getLogger(ProductControllerCircuit.class);
    private final ServiceState serviceState;

    public ProductControllerCircuit(ServiceState serviceState) {
        this.serviceState = serviceState;
    }

    @GetMapping("/v1/products/{productId}")
    public ResponseEntity<Map<String, Boolean>> getStock(@PathVariable("productId") String productId) {
        if (!serviceState.isHealthy()) {
            log.warn("서비스가 '고장' 상태입니다. 503 Service Unavailable 반환.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        log.info("정상 응답. 재고 있음(true) 반환. productId: {}", productId);
        return ResponseEntity.ok(Map.of("hasStock", true));
    }

    @PostMapping("/v1/products/break")
    public ResponseEntity<String> breakService() {
        log.error("외부 요청에 의해 서비스가 '고장' 상태로 전환됩니다.");
        serviceState.breakService();
        return ResponseEntity.ok("서비스가 고장 상태로 전환되었습니다.");
    }

    @PostMapping("/v1/products/fix")
    public ResponseEntity<String> fixService() {
        log.info("외부 요청에 의해 서비스가 '정상' 상태로 복구됩니다.");
        serviceState.fixService();
        return ResponseEntity.ok("서비스가 정상 상태로 복구되었습니다.");
    }
}
