package com.Alizone.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Alizone.Entity.Order;
import com.Alizone.Entity.User;
import com.Alizone.Enum.OrderStatus;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	@Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.olusturmatarihi DESC")
    Optional<Order> findTopByUserIdOrderByOlusturmatarihiDesc(Long userId);
	List<Order> findByUser(User user);
	List<Order> findBySiparisdurumuAndOlusturmatarihiBefore(
	        OrderStatus status,
	        LocalDateTime time
	);
	List<Order> findBySiparisdurumu(OrderStatus siparisdurumu);
	
	

}
