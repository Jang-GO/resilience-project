package janggo.practice.callerservice.circuitbreaker;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import janggo.practice.callerservice.retry.dto.StockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ProductApiClientWithCircuit {
    private static final Logger log = LoggerFactory.getLogger(ProductApiClientWithCircuit.class);
    private final RestClient restClient;

    public ProductApiClientWithCircuit(RestClient restClient) {
        this.restClient = restClient;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackForStockCheck")
    public boolean checkStock(String productId) {
        log.info("--> 상품 서비스에 재고 확인 요청...");
        String url = "http://localhost:8080/v1/products/" + productId;
        StockResponse response = restClient.get()
                .uri(url)
                .retrieve()
                .body(StockResponse.class);
        log.info("<-- 재고 확인 성공");
        return response != null && response.hasStock();
    }

    public boolean fallbackForStockCheck(String productId, Throwable t) {
        log.warn("### 서킷 브레이커 OPEN ### Fallback 실행. productId: {}, error: {}", productId, t.getClass().getSimpleName());
        return false;
    }
}