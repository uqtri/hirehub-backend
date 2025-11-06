package org.example.hirehub.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class HttpClientService {

    private final RestTemplate restTemplate;

    public HttpClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Generic GET request
    public <T> T get(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }
    public <T> T get(String url, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<T> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                responseType
        );

        return response.getBody();
    }

    // Generic POST request
    public <T> T post(String url, Object requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<T> response = restTemplate.postForEntity(url, entity, responseType);
        return response.getBody();
    }
    public <T> T post(String url, Object requestBody, HttpHeaders headers, Class<T> responseType) {

        // 1. Tạo HttpEntity chứa requestBody và headers
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        // 2. Sử dụng postForEntity()
        ResponseEntity<T> response = restTemplate.postForEntity(url, entity, responseType);

        // (Bổ sung kiểm tra status code nếu cần)
        return response.getBody();
    }
}
