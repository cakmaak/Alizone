package com.Alizone.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.Alizone.Entity.Order;
import com.Alizone.Entity.OrderItem;
import com.Alizone.Enum.OrderStatus;
import com.Alizone.Repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCleanupJob {

    private final OrderRepository orderRepository;

    // ⏱ 30 dakikada bir
    @Scheduled(fixedRate = 30 * 60 * 1000)
    @Transactional
    public void cancelExpiredOrders() {

        LocalDateTime expireTime =
                LocalDateTime.now().minusMinutes(30);

        List<Order> expiredOrders =
                orderRepository
                        .findBySiparisdurumuAndOlusturmatarihiBefore(
                                OrderStatus.PENDING,
                                expireTime
                        );

        for (Order order : expiredOrders) {

            for (OrderItem item : order.getItemlist()) {

                int newReserved =
                        item.getProduct().getReservedStock() - item.getAdet();

                item.getProduct().setReservedStock(
                        Math.max(newReserved, 0)
                );
            }

            order.setSiparisdurumu(OrderStatus.CANCELLED);
            orderRepository.save(order); // 🔥 ÖNEMLİ
        }
    }
}

