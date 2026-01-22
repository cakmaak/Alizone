package com.Alizone.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Alizone.Dto.RefundRequest;

public interface RefundRepository extends JpaRepository<RefundRequest, Long> {
	 Optional<RefundRequest> findByOrderId(Long orderId);

	public  boolean existsByOrderId(Long orderId);
	
	

}
