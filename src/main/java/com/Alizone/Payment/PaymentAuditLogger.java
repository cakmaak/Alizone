package com.Alizone.Payment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.Alizone.Repository.PaymentAuditRepository;
import org.apache.commons.codec.digest.DigestUtils;

@Component
public class PaymentAuditLogger {

    private static final Logger log = LoggerFactory.getLogger("PAYMENT_AUDIT");

    @Autowired
    private PaymentAuditRepository paymentAuditRepository;

    // Basit log (IP ve UA yok)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(PaymentEvent event, Long orderId, Long userId, String message) {
        log(event, orderId, userId, message, null, null);
    }

    // Prod log
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(
            PaymentEvent event,
            Long orderId,
            Long userId,
            String message,
            String ipAddress,
            String userAgent
    ) {
        log.info("{} | orderId={} | userId={} | {} | ip={} | ua={}",
                event.name(), orderId, userId, message, ipAddress, userAgent);

        PaymentAudit audit = new PaymentAudit();
        audit.setEvent(event.name());
        audit.setOrderId(orderId);
        audit.setUserId(userId);
        audit.setMessage(message);
        audit.setIpAddress(ipAddress);
        audit.setUserAgent(userAgent);

        
        ZonedDateTime istanbulNow = ZonedDateTime.now(ZoneId.of("Europe/Istanbul"));
        audit.setEventTime(istanbulNow.toLocalDateTime());
        audit.setLoggedAt(istanbulNow.toLocalDateTime());

        // Hash
        String hashInput = orderId + "|" + userId + "|" + event.name() + "|" + audit.getEventTime() + "|" + message;
        audit.setHash(DigestUtils.sha256Hex(hashInput));

        paymentAuditRepository.save(audit);
    }
        
    }
