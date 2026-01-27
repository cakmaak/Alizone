package com.Alizone.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import com.Alizone.Dto.RefundRequest;
import com.Alizone.Entity.Order;
import com.Alizone.Entity.User;
import com.Alizone.Enum.OrderStatus;
import com.Alizone.Enum.RefundStatus;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Payment.PaymentAuditLogger;
import com.Alizone.Payment.PaymentEvent;
import com.Alizone.Repository.OrderRepository;
import com.Alizone.Repository.RefundRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class OrderCancelService {
	
	
	
	@Autowired
	private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private PaymentAuditLogger paymentAuditLogger;

    @Transactional
    public Order cancelOrder(Long orderId,String ip, String userAgent) {

        User user = getCurrentUser();

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("Sipariş bulunamadı"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Bu siparişi iptal edemezsiniz");
        }

        switch (order.getSiparisdurumu()) {
            case PENDING -> cancelPendingOrder(order, ip, userAgent);
            case PAID -> refundPaidOrder(order,ip,userAgent);
            case SHIPPED, DELIVERED ->
                throw new BusinessException("Kargoya verilen sipariş iptal edilemez");
            default ->
                throw new BusinessException("Bu sipariş iptal edilemez");
        }

        return orderRepository.save(order);
    }

    private void cancelPendingOrder(Order order,String ip,String ua) {

        order.setSiparisdurumu(OrderStatus.CANCELLED);
        

        paymentAuditLogger.log(
                PaymentEvent.ORDER_CANCELLED,
                order.getId(),
                order.getUser().getId(),
                "pending_cancel",
                ip,
                ua
            );
        mailService.buildOrderCancelledMail(order);
        }
    
    

    private void refundPaidOrder(Order order,String ip, String userAgent) {

        order.setSiparisdurumu(OrderStatus.REFUND_PENDING);

        RefundRequest refund = new RefundRequest();
        refund.setOrderId(order.getId());
        refund.setPaymentId(order.getBankPaymentId());
        refund.setAmount(order.getToplamtutar());
        refund.setStatus(RefundStatus.PROCESSING);
        refund.setCreatedAt(LocalDateTime.now());

        refundRepository.save(refund);

        paymentAuditLogger.log(
                PaymentEvent.REFUND_REQUESTED,
                order.getId(),
                order.getUser().getId(),
                "amount=" + order.getToplamtutar(),
                ip,
                userAgent
            );
        mailService.sendOrderCancelledMails(order);
    }

    private User getCurrentUser() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BusinessException("Lütfen giriş yapınız.");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof User user) return user;

        if (principal instanceof UserDetails ud)
            return userService.getUserbyEmail(ud.getUsername());

        throw new BusinessException("Kullanıcı doğrulanamadı");
    }
    @Transactional
    public void handleRefundCallback(Long refundId, boolean success) {

        RefundRequest refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new BusinessException("Refund bulunamadı"));

        Order order = orderRepository.findById(refund.getOrderId())
            .orElseThrow(() -> new BusinessException("Sipariş bulunamadı"));

        if (success) {
            refund.setStatus(RefundStatus.COMPLETED);
            refund.setCompletedAt(LocalDateTime.now());
            order.setSiparisdurumu(OrderStatus.REFUNDED);

            paymentAuditLogger.log(
                PaymentEvent.REFUND_SUCCESS,
                order.getId(),
                order.getUser().getId(),
                "refundId=" + refundId
            );
        } else {
            refund.setStatus(RefundStatus.FAILED);
            order.setSiparisdurumu(OrderStatus.PAID);
        }

        refundRepository.save(refund);
        orderRepository.save(order);
    }
    
    @Transactional
    public void startRefundByAdmin(Long orderId) {

        // 🔒 aynı siparişe 2 refund açılmasın
        if (refundRepository.existsByOrderId(orderId)) {
            throw new BusinessException("Bu sipariş için zaten refund mevcut");
        }

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("Sipariş bulunamadı"));

        if (order.getSiparisdurumu() != OrderStatus.PAID
            && order.getSiparisdurumu() != OrderStatus.CANCELLED) {
            throw new BusinessException("Bu sipariş iade edilemez");
        }

        order.setSiparisdurumu(OrderStatus.REFUND_PENDING);

        RefundRequest refund = new RefundRequest();
        refund.setOrderId(order.getId());
        refund.setPaymentId(order.getBankPaymentId());
        refund.setAmount(order.getToplamtutar());
        refund.setStatus(RefundStatus.PROCESSING);
        refund.setCreatedAt(LocalDateTime.now());

        refundRepository.save(refund);
        orderRepository.save(order);

        paymentAuditLogger.log(
            PaymentEvent.REFUND_REQUESTED,
            order.getId(),
            order.getUser().getId(),
            "admin_refund"
        );
    }
    @Transactional
    public void handleBankRefundCallback(Long orderId, boolean success) {

        RefundRequest refund = refundRepository
            .findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("Refund bulunamadı"));

        if (refund.getStatus() != RefundStatus.PROCESSING) {
            throw new BusinessException("Geçersiz refund durumu");
        }

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("Sipariş bulunamadı"));

        if (success) {
            refund.setStatus(RefundStatus.COMPLETED);
            refund.setCompletedAt(LocalDateTime.now());
            order.setSiparisdurumu(OrderStatus.REFUNDED);

            paymentAuditLogger.log(
                PaymentEvent.REFUND_SUCCESS,
                order.getId(),
                order.getUser().getId(),
                "bank_callback"
            );
        } else {
            refund.setStatus(RefundStatus.FAILED);
            order.setSiparisdurumu(OrderStatus.PAID);
        }

        refundRepository.save(refund);
        orderRepository.save(order);
    }
}