package com.Alizone.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alizone.Payment.PaymentAudit;

@Repository
public interface PaymentAuditRepository extends JpaRepository<PaymentAudit, Long> {

}
