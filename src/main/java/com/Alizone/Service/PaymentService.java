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

    @Transactional
    public void processCallback(
            Long orderId,
            boolean success,
            Long timestamp,
            String paymentId,
            String incomingSignature) {
    	
    	paymentAuditLogger.log(
    		    PaymentEvent.PAYMENT_CALLBACK_RECEIVED,
    		    orderId,
    		    null,
    		    "paymentId=" + paymentId
    		);

        //  İMZA DATA (BANKA NE GÖNDERİYORSA AYNI FORMAT)
        String data = orderId + "|" + success + "|" + timestamp + "|" + paymentId ;

        // 2️ BACKEND’DE YENİDEN İMZA ÜRET
        String expectedSignature =
                HmacUtil.hmacSha256(data, callbackSecret);

        // 3️ İMZA DOĞRULA
        if (!expectedSignature.equals(incomingSignature)) {
            throw new BusinessException("Geçersiz imza ❌");
        }
        paymentAuditLogger.log(
        	    PaymentEvent.PAYMENT_CALLBACK_VERIFIED,
        	    orderId,
        	    null,
        	    "signature=true"
        	);

        // 4️ ORDER VAR MI?
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Sipariş bulunamadı"));

        // 5️ IDEMPOTENCY (ÇOK KRİTİK)
        if (order.getSiparisdurumu() == OrderStatus.PAID) {
            return; 
        }

        // 6️ GERÇEK İŞİ YAPAN SERVİS
        orderItemService.handlePaymentCallback(orderId, success,paymentId);
        paymentAuditLogger.log(
        	    PaymentEvent.PAYMENT_SUCCESS,
        	    orderId,
        	    order.getUser().getId(),
        	    "paymentId=" + paymentId
        	);
    }
    
	
	public String testSign(Long orderId, boolean success, long timestamp,String paymentId) {
		String data = orderId + "|" + success + "|" + timestamp + "|" + paymentId;
	    return HmacUtil.hmacSha256(data,callbackSecret );
	}
}
