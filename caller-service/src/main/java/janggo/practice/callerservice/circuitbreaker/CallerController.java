package janggo.practice.callerservice.circuitbreaker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallerController {
    private final ProductApiClientWithCircuit apiClient;

    public CallerController(ProductApiClientWithCircuit apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/products/{productId}")
    public String orderProduct(@PathVariable("productId") String productId) {
        boolean hasStock = apiClient.checkStock(productId);
        return hasStock ? "상품 재고 있음" : "상품 재고 없음 또는 서비스 오류";
    }
}

