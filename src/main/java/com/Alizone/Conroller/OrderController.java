package com.Alizone.Conroller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Alizone.Dto.DtoOrder;
import com.Alizone.Dto.DtoOrderRequest;
import com.Alizone.Entity.Basket;
import com.Alizone.Entity.Order;
import com.Alizone.Enum.OrderStatus;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Mapper.OrderMapper;
import com.Alizone.Repository.BasketRepository;
import com.Alizone.Service.IOrderItemService;
import com.Alizone.Service.MailService;
import com.Alizone.Service.OrderCancelService;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/alizone")
public class OrderController implements IOrderController{

    @Autowired
    private MailService mailService;

   @Autowired
  private  OrderCancelService orderCancelService;
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private IOrderItemService orderItemService;
	
	@Autowired
	private BasketRepository basketRepository;



  
	
	@PreAuthorize("isAuthenticated()")
	@Override
	@PostMapping("/createorder")
	public ResponseEntity<Long> createOrderfrombasket(
	        @RequestBody DtoOrderRequest request,
	        HttpServletRequest httpRequest
	) {
	  

		Long orderId = orderItemService.saveOrderitemfromBasket(request, httpRequest);
	    return ResponseEntity.ok(orderId);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/my-orders")
	@Override
	public List<DtoOrder> findOrderitem() {
		List<Order> orders = orderItemService.findOrderitem();

	    return orders.stream()
	            .map(orderMapper::toDto)
	            .collect(Collectors.toList());
	}
	@GetMapping("/success")
	public ResponseEntity<?> orderSuccess(@RequestParam Long orderId) {
	    Order order = orderItemService.findOrderById(orderId);

	    boolean paid = order.getSiparisdurumu() == OrderStatus.PAID;

	    Basket basket = basketRepository
	            .findByUser(order.getUser())
	            .orElseThrow(() -> new BusinessException("Sepet bulunamadı"));

	   
	    basket.getBasketItems().forEach(item -> item.setActive(false));
	    basketRepository.save(basket);

	    return ResponseEntity.ok(Map.of(
	        "orderId", order.getId(),
	        "paid", paid
	    ));
	    
	}
	 @GetMapping("/status/{orderId}")
	    public ResponseEntity<?> getOrderStatus(@PathVariable Long orderId) {
	        try {
	            Order order = orderItemService.findOrderById(orderId);

	            if (order == null) {
	                return ResponseEntity.status(404).body(Map.of("error", "Order not found"));
	            }

	            boolean paid = order.getSiparisdurumu() == OrderStatus.PAID;

	            return ResponseEntity.ok(Map.of(
	                "orderId", order.getId(),
	                "paid", paid
	            ));
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
	        }
	    
	}

	 @Override
	 @PostMapping("/{orderId}/cancel")
	 @PreAuthorize("isAuthenticated()")
	 public ResponseEntity<?> cancelOrder(
	         @PathVariable Long orderId,
	         HttpServletRequest request
	 ) {

	     String ip = getClientIp(request);
	     String ua = request.getHeader("User-Agent");

	     Order order = orderCancelService.cancelOrder(orderId, ip, ua);

	     //mailService.sendOrderCancelledMails(order);

	     return ResponseEntity.ok(Map.of(
	         "success", true,
	         "message", "Sipariş iptal süreci başlatıldı",
	         "orderId", order.getId(),
	         "status", order.getSiparisdurumu()
	     ));
	 }
	 private String getClientIp(HttpServletRequest request) {
	        String xf = request.getHeader("X-Forwarded-For");
	        if (xf != null && !xf.isBlank()) {
	            return xf.split(",")[0].trim();
	        }
	        return request.getRemoteAddr();
	    }

	
}
	

	
	
	


