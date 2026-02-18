package com.Alizone.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.Alizone.Dto.ConfirmPaymentRequest;
import com.Alizone.Dto.Halk3DRequestDto;
import com.Alizone.Entity.Order;
import com.Alizone.Enum.OrderStatus;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Payment.PaymentAuditLogger;
import com.Alizone.Payment.PaymentEvent;
import com.Alizone.Repository.OrderRepository;
import com.Alizone.Dto.ConfirmPaymentResponse;


import Payment.HalkHashGenerator;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PaymentService {

	@Autowired
	private HalkHashGenerator halkHashGenerator;
	
	@Value("${halk.merchant-id}")
	private String merchantId;
	
	@Value("${halk.merchant-key}")
	private String merchantKey;
	
	@Value("${halk.success-url}")
	private String successUrl; 
	
	@Value("${halk.fail-url}")
	private String failUrl;
    
    @Autowired
    private PaymentAuditLogger paymentAuditLogger;


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IOrderItemService orderItemService;
    

   
    @Transactional
    public Halk3DRequestDto startHalkPayment(Long orderId) throws Exception {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order bulunamadı"));

        if (order.getSiparisdurumu() != OrderStatus.PENDING) {
            throw new BusinessException("Order zaten işlenmiş");
        }

        String total = order.getToplamtutar().toPlainString();
        String installment = "1";
        String currency = "TRY";
        String invoiceId = order.getId().toString();

        String hash = halkHashGenerator.generateHash(
                total,
                installment,
                currency,
                invoiceId
        );

        order.setPaymentProvider("HALKODEME");
        orderRepository.save(order);

        return Halk3DRequestDto.builder()
                .total(total)
                .installment(installment)
                .currencyCode(currency)
                .merchantKey(merchantKey)
                .merchantId(merchantId)
                .invoiceId(invoiceId)
                .hash(hash)
                .successUrl(successUrl)
                .failUrl(failUrl)
                .build();
    }
    @Transactional
    public void confirmPayment(String invoiceId, Double total, String status) throws Exception {

        Order order = orderRepository.findById(Long.valueOf(invoiceId))
                .orElseThrow(() -> new BusinessException("Order bulunamadı"));

        ConfirmPaymentRequest request = ConfirmPaymentRequest.builder()
                .invoice_id(invoiceId)
                .total(total)
                .status(status)
                .merchant_key(merchantKey)
                .build();

        String hash = halkHashGenerator.generateConfirmHash(
                merchantKey,
                invoiceId,
                status
        );

        request.setHash_key(hash);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ConfirmPaymentRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<ConfirmPaymentResponse> response =
                restTemplate.postForEntity(
                        "https://testapp.halkode.com.tr/api/confirmPayment",
                        entity,
                        ConfirmPaymentResponse.class
                );
        if (response.getBody() != null && response.getBody().getSuccess()) {
            orderItemService.handlePaymentCallback(order.getId(), true, "HALK");
        } else {
            orderItemService.handlePaymentCallback(order.getId(), false, null);
        }
    }
        
    
}