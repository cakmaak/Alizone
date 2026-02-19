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
    
    @Autowired
    private HalkPaymentService halkPaymentService;

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
    public String createRealPurchaseLink(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order bulunamadı"));

        PurchaseLinkResponse response = halkPaymentService.createPurchaseLink(order);

        if (response == null || response.getLink() == null) {
            throw new RuntimeException("Purchase link alınamadı");
        }

        return response.getLink();
    }
    }