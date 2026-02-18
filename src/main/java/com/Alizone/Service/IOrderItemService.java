package com.Alizone.Service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.Alizone.Dto.DtoOrderRequest;
import com.Alizone.Entity.Address;
import com.Alizone.Entity.Order;
import com.Alizone.Entity.User;

import jakarta.servlet.http.HttpServletRequest;

public interface IOrderItemService {
	
	public Long saveOrderitemfromBasket(DtoOrderRequest request,HttpServletRequest httpRequest);
	public List<Order> findOrderitem();
	public Long getLastCreatedOrderIdForUser(User user);
	public void handlePaymentCallback(Long orderId, boolean success,String paymentId);
	public ResponseEntity<?> changeOrderStatus(Long id,String cargoTrackingNo);
	public List<Order> getallorders();
	public Order findOrderById(Long orderId);
	

}
