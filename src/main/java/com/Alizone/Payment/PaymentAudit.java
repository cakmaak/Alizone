package com.Alizone.Payment;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payment_audit")
@Data
public class PaymentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String event;
    private Long orderId;
    private Long userId;

    @Column(length = 1000)
    private String message;

    // 🔥 YENİ
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;
    
    @Column(name = "logged_at")
    private LocalDateTime loggedAt;

    @Column(name = "event_time")
    private LocalDateTime eventTime;

    private LocalDateTime createdAt;

    private String hash;
}