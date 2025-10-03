package janggo.practice.callerservice.retry;

import janggo.practice.callerservice.retry.dto.StockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

@Service
public class ProductApiClient {

    private static final Logger log = LoggerFactory.getLogger(ProductApiClient.class);
    private final RestClient restClient;

    public ProductApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Retryable(
            retryFor = {ResourceAccessException.class, HttpServerErrorException.class},
            maxAttempts = 3,
            noRetryFor = {HttpClientErrorException.class},
            backoff = @Backoff(delay=1000)
    )
    public boolean checkStock(String productId) {
        log.info("상품 서비스에 재고 확인을 요청합니다. productId: {}", productId);
        String url = "http://localhost:8080/products/" + productId + "/stock";
        StockResponse response = restClient.get()
                .uri(url)
                .retrieve()
                .body(StockResponse.class);

        if (response == null) {
            return false;
        }

        return response.hasStock();
    }

    @Recover
    public boolean recover(ResourceAccessException e, String productId) {
        log.error("모든 재시도 실패 (ResourceAccessException). productId: {}. Error: {}", productId, e.getMessage());
        return false;
    }

    // 👇 [수정 2] HttpServerErrorException을 위한 복구 메서드 추가
    @Recover
    public boolean recover(HttpServerErrorException e, String productId) {
        log.error("모든 재시도 실패 ({}). productId: {}. Error: {}", e.getStatusCode(), productId, e.getMessage());
        return false;
    }

    @Recover
    public boolean recover(HttpClientErrorException e, String productId) {
        log.error("재시도 불가 예외 발생 ({}). productId: {}. Error: {}", e.getStatusCode(), productId, e.getMessage());
        return false;
    }

    @Recover
    public boolean recover(RestClientException e, String productId) {
        log.error("응답 처리 중 예외 발생. productId: {}. Error: {}", productId, e.getMessage());
        return false;
    }
}
