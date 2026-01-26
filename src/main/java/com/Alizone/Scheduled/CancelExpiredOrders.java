package com.Alizone.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.Alizone.Entity.Order;
import com.Alizone.Entity.OrderItem;
import com.Alizone.Entity.Product;
import com.Alizone.Enum.OrderStatus;
import com.Alizone.Repository.OrderRepository;
import com.Alizone.Service.MailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CancelExpiredOrders {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    // ⏱️ 5 dakikada bir çalışır
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void cancelExpiredOrders() {

        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(30);

        List<Order> expiredOrders =
                orderRepository.findBySiparisdurumuAndOlusturmatarihiBefore(
                        OrderStatus.PENDING,
                        expireTime
                );

        for (Order order : expiredOrders) {

            // 🔓 RESERVED STOCK GERİ AL
            for (OrderItem item : order.getItemlist()) {
                Product product = item.getProduct();
                product.setReservedStock(
                        product.getReservedStock() - item.getAdet()
                );
            }

            order.setSiparisdurumu(OrderStatus.CANCELLED);

            //📩 KULLANICIYA MAIL
            try {
                //mailService.sendHtmlMail(
                    //order.getUser().getEmail(),
                   // "❌ Siparişiniz İptal Edildi"
                   // mailService.buildOrderCancelledMail(order)
                //);
            } catch (Exception e) {
                // ❌ mail gitmezse sistem çökmesin
                e.printStackTrace();
            }
          
        }
    }
}
