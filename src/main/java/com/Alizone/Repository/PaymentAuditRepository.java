package com.Alizone.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Alizone.Payment.PaymentAudit;

public interface PaymentAuditRepository extends JpaRepository<PaymentAudit, Long> {

}
