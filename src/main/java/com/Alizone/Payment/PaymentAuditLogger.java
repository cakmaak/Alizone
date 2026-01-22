package com.Alizone.Payment;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.Alizone.Repository.PaymentAuditRepository;

@Component
public class PaymentAuditLogger {

    private static final Logger log =
            LoggerFactory.getLogger("PAYMENT_AUDIT");

    @Autowired
    private PaymentAuditRepository paymentAuditRepository;

    // 🔁 ESKİ KODLAR BOZULMASIN DİYE
    public void log(
            PaymentEvent event,
            Long orderId,
            Long userId,
            String message
    ) {
        log(event, orderId, userId, message, null, null);
    }

    // 🔥 ASIL PROD METOD
    public void log(
            PaymentEvent event,
            Long orderId,
            Long userId,
            String message,
            String ipAddress,
            String userAgent
    ) {
        // FILE LOG
        log.info(
            "{} | orderId={} | userId={} | {} | ip={} | ua={}",
            event.name(),
            orderId,
            userId,
            message,
            ipAddress,
            userAgent
        );

        // DB LOG
        PaymentAudit audit = new PaymentAudit();
        audit.setEvent(event.name());
        audit.setOrderId(orderId);
        audit.setUserId(userId);
        audit.setMessage(message);
        audit.setIpAddress(ipAddress);
        audit.setUserAgent(userAgent);
        audit.setCreatedAt(LocalDateTime.now());

        paymentAuditRepository.save(audit);
    }
}