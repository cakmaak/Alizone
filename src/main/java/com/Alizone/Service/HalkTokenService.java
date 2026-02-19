package com.Alizone.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HalkTokenService {

    @Value("${halk.base-url}")
    private String baseUrl;

    @Value("${halk.app-key}")
    private String appKey;

    @Value("${halk.app-secret}")
    private String appSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public String getToken() {

        String url = baseUrl + "/api/token"; // dikkat: '/' ekledik

        Map<String, String> request = new HashMap<>();
        request.put("app_key", appKey);
        request.put("app_secret", appSecret);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Token alınamadı, status: " + response.getStatusCode());
        }

        Map<String, Object> body = response.getBody();
        return body.get("token").toString();
        
    }
}