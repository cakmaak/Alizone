package com.Alizone.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import com.Alizone.Entity.Order;
import com.Alizone.Enum.OrderStatus;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Payment.PaymentAuditLogger;
import com.Alizone.Payment.PaymentEvent;
import com.Alizone.Repository.OrderRepository;
import com.Alizone.Security.HmacUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PaymentService {

    @Value("${payment.callback-secret}")
    private String callbackSecret;
    
    @Autowired
    private PaymentAuditLogger paymentAuditLogger;


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IOrderItemService orderItemService;
    
    @Autowired
    private HttpServletRequest request;

    
    @Transactional
    public void processCallback(
            Long orderId,
            boolean success,
            Long timestamp,
            String paymentId,
            String incomingSignature) {

        // IP alma
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) ipAddress = request.getRemoteAddr();

        String userAgent = request.getHeader("User-Agent"); 
        // Order bul
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Sipariş bulunamadı"));

        Long userId = order.getUser() != null ? order.getUser().getId() : null;

        // CALLBACK ALINDI
        paymentAuditLogger.log(
            PaymentEvent.PAYMENT_CALLBACK_RECEIVED,
            orderId,
            userId,
            "paymentId=" + paymentId,
            ipAddress,
            userAgent
        );

        // İmza kontrol
        String data = orderId + "|" + success + "|" + timestamp + "|" + paymentId;
        String expectedSignature = HmacUtil.hmacSha256(data, callbackSecret);
        if (!expectedSignature.equals(incomingSignature)) {
            throw new BusinessException("Geçersiz imza ❌");
        }

        paymentAuditLogger.log(
            PaymentEvent.PAYMENT_CALLBACK_VERIFIED,
            orderId,
            userId,
            "signature=true",
            ipAddress,
            userAgent
        );

        // IDEMPOTENCY
        if (order.getSiparisdurumu() == OrderStatus.PAID) return;

        // İşleme
        orderItemService.handlePaymentCallback(orderId, success, paymentId);

        paymentAuditLogger.log(
            PaymentEvent.PAYMENT_SUCCESS,
            orderId,
            userId,
            "paymentId=" + paymentId,
            ipAddress,
            userAgent
        );
    }
	
	public String testSign(Long orderId, boolean success, long timestamp,String paymentId) {
		String data = orderId + "|" + success + "|" + timestamp + "|" + paymentId;
	    return HmacUtil.hmacSha256(data,callbackSecret );
	}
}
