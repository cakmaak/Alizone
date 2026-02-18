package com.Alizone.Service;


import com.Alizone.Dto.PurchaseLinkRequest;
import com.Alizone.Dto.PurchaseLinkResponse;
import com.Alizone.Entity.Order;
import com.Alizone.Enum.OrderStatus;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {

    @Autowired
    private HalkTokenService halkTokenService;

  

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IOrderItemService orderItemService;

    @Value("${halk.merchant-id}")
    private String merchantId;

    @Value("${halk.merchant-key}")
    private String merchantKey;

    @Value("${halk.success-url}")
    private String successUrl;

    @Value("${halk.fail-url}")
    private String failUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // ===============================
    // 1️⃣ 3D ÖDEME BAŞLAT
    // ===============================
    @Transactional
    public String createPurchaseLink(Long orderId) throws Exception {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order bulunamadı"));

        String token = halkTokenService.getToken();

        String invoiceJson = """
            {
              "invoice_id":"%s",
              "invoice_description":"Order Payment",
              "total":%s,
              "return_url":"%s",
              "cancel_url":"%s",
              "items":[
                {
                  "name":"Order Item",
                  "price":%s,
                  "quantity":1,
                  "description":"Test"
                }
              ]
            }
            """.formatted(
                order.getId(),
                order.getToplamtutar(),
                successUrl,
                failUrl,
                order.getToplamtutar()
            );

        PurchaseLinkRequest request = new PurchaseLinkRequest();
        request.setMerchant_key(merchantKey);
        request.setCurrency_code("TRY");
        request.setInvoice(invoiceJson);
        request.setName("Test");
        request.setSurname("User");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PurchaseLinkRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<PurchaseLinkResponse> response =
                restTemplate.postForEntity(
                        "https://testapp.halkode.com.tr/ccpayment/purchase/link",
                        entity,
                        PurchaseLinkResponse.class
                );

        if (response.getBody() != null && Boolean.TRUE.equals(response.getBody().getStatus())) {
            return response.getBody().getLink();
        }

        throw new RuntimeException("Purchase link oluşturulamadı");
    }
    }