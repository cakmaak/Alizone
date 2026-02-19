package com.Alizone.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.Alizone.Dto.PurchaseLinkResponse;
import com.Alizone.Entity.Order;

@Service
public class HalkPaymentService {

    @Value("${halk.base-url}")
    private String baseUrl;

    @Value("${halk.merchant-key}")
    private String merchantKey;

    @Autowired
    private HalkTokenService halkTokenService;
    
    
    private final RestTemplate restTemplate = new RestTemplate();



    public PurchaseLinkResponse createPurchaseLink(Order order) {

        String token = halkTokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("merchant_key", merchantKey);
        body.put("currency_code", "TRY");
        body.put("invoice", buildInvoiceJson(order));
        body.put("name", order.getUser().getIsim());
        body.put("surname", order.getUser().getSoyisim());

        HttpEntity<Map<String,Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<PurchaseLinkResponse> response =
                restTemplate.postForEntity(
                        baseUrl + "/purchase/link",
                        request,
                        PurchaseLinkResponse.class);

        return response.getBody();
    }

    private String buildInvoiceJson(Order order) {

        return "{"
                + "\"invoice_id\":\"" + order.getId() + "\","
                + "\"invoice_description\":\"Alizone Sipariş\","
                + "\"total\":" + order.getToplamtutar() + ","
                + "\"return_url\":\"https://alizoneklima.com/payment/success\","
                + "\"cancel_url\":\"https://alizoneklima.com/payment/fail\","
                + "\"items\":["
                + "{"
                + "\"name\":\"Sipariş\","
                + "\"price\":" + order.getToplamtutar() + ","
                + "\"quantity\":1,"
                + "\"description\":\"Alizone Klima Siparişi\""
                + "}"
                + "]"
                + "}";
    }

}