package janggo.practice.callerservice.timeout.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class OrderController {
    private final RestTemplate restTemplate;
    private final WebClient webClient;

    private final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(RestTemplateBuilder restTemplateBuilder, WebClient.Builder webClientBuilder){
        this.restTemplate = restTemplateBuilder.build();
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    @GetMapping("/orders/product-details-rt/{productId}")
    public ResponseEntity<String> getProductDetailsForOrderRestTemplate(@PathVariable("productId") String productId){
        String url = "http://localhost:8080/products/" + productId;
        log.info("[RestTemplate] 상품 정보 서비스에 상세 정보를 요청합니다.");
        String productInfo = restTemplate.getForObject(url, String.class);
        log.info("[RestTemplate] 상품 정보를 성공적으로 받아왔습니다.");
        return ResponseEntity.ok("[주문 서비스]" + productInfo);
    }

    @GetMapping("/orders/product-details-wc/{productId}")
    public Mono<ResponseEntity<String>> callWithWebClient(@PathVariable("productId") String productId){
        log.info("[WebClient] 상품 정보 서비스에 상세 정보를 요청합니다.");
        return webClient.get()
                .uri("/products/{productId}", productId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(result -> log.info("[WebClient] 상품 정보를 성공적으로 받아왔습니다."))
                .map(result -> ResponseEntity.ok("[주문 서비스]" + result));
    }
}
