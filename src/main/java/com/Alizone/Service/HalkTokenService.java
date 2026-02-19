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

        String url = baseUrl + "/api/token";

        Map<String, String> request = new HashMap<>();
        request.put("app_id", appKey);
        request.put("app_secret", appSecret);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> body = response.getBody();

        if (body == null) {
            throw new RuntimeException("Token response boş");
        }

        Object dataObj = body.get("data");
        if (!(dataObj instanceof Map)) {
            throw new RuntimeException("Token data alanı yok: " + body);
        }

        Map<String, Object> data = (Map<String, Object>) dataObj;

        String token = data.get("token").toString();

        System.out.println("ALINAN TOKEN: " + token);

        return token;
    }
    public void testToken() {
        String token = getToken();
        System.out.println("HalkÖde Token: " + token);
    }
}