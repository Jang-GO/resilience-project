package janggo.practice.calleeservice.timeout.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductInfoController {

    private final Logger log = LoggerFactory.getLogger(ProductInfoController.class);

    @GetMapping("/products/{productId}")
    public ResponseEntity<String> getProductDetails(@PathVariable("productId") String productId) throws InterruptedException {
        log.info("상품 상세 정보 조회 요청 받음: {}", productId);
        String productInfo = getProductInfo(productId);
        return ResponseEntity.ok(productInfo);
    }

    private String getProductInfo(String productId) throws InterruptedException {
        // 무거운 DB 조회 및 불안정한 서버 상황 등으로 5초가 걸린다고 가정
        Thread.sleep(5000);
        return "상품 ID: " + productId;
    }

}
