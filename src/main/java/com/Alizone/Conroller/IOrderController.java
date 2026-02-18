package com.Alizone.Conroller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.Alizone.Dto.DtoOrder;
import com.Alizone.Dto.DtoOrderRequest;
import com.Alizone.Entity.Address;
import com.Alizone.Entity.Order;

import jakarta.servlet.http.HttpServletRequest;


public interface IOrderController {
	
	public ResponseEntity<Map<String, String>> createOrderfrombasket(DtoOrderRequest request, HttpServletRequest httpRequest);
	public List<DtoOrder> findOrderitem();
	public ResponseEntity<?> cancelOrder(Long orderId,HttpServletRequest request);
	
	
		
	

}
