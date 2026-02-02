package com.Alizone.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.Alizone.Dto.DtoOrderItem;
import com.Alizone.Dto.DtoOrderRequest;
import com.Alizone.Entity.Address;
import com.Alizone.Entity.Basket;
import com.Alizone.Entity.BasketItem;
import com.Alizone.Entity.Order;
import com.Alizone.Entity.OrderItem;
import com.Alizone.Entity.Product;
import com.Alizone.Entity.User;
import com.Alizone.Enum.OrderStatus;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Payment.PaymentAuditLogger;
import com.Alizone.Payment.PaymentEvent;
import com.Alizone.Repository.BasketRepository;
import com.Alizone.Repository.OrderRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemService implements IOrderItemService {

	@Autowired
	private PaymentAuditLogger paymentAuditLogger;

	private static final Logger adminLogger = LoggerFactory.getLogger("adminLogger");

	@Value("${mail.admin.address}")
	private String adminMail;

	@Autowired
	private IUserService userService;

	@Autowired
	private IBasketService basketService;

	@Autowired
	private BasketRepository basketRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private IAddressService addressService;

	@Override
	@Transactional
	public String saveOrderitemfromBasket(DtoOrderRequest request, HttpServletRequest httpRequest) {
		
		String clientIp = getClientIp(httpRequest);
		String userAgent = httpRequest.getHeader("User-Agent");

		if (request.getContractsAccepted() == null || !request.getContractsAccepted()) {
			throw new BusinessException("Mesafeli satış sözleşmesi ve KVKK onaylanmadan sipariş verilemez.");
		}

		User user = getAuthenticatedUser();
		Address address;
		if (request.getAddressId() != null) {
			address = addressService.findById(request.getAddressId());
		} else if (request.getAddress() != null) {
			Address newAddress = request.getAddress();
			newAddress.setUser(user);
			address = addressService.saveAddress(newAddress);
		} else {
			throw new BusinessException("Lütfen bir adres seçin veya yeni adres ekleyin.");
		}

		if (!"ANKARA".equalsIgnoreCase(address.getSehir())) {
			throw new BusinessException("Şu an sadece Ankara içi sipariş kabul ediyoruz.");
		}

		Basket basket = basketService.getOrCreateBasketByUser(user);

		List<BasketItem> activeItems = basket.getBasketItems().stream().filter(BasketItem::isActive).toList();

		if (activeItems.isEmpty()) {
			throw new BusinessException("Sepetiniz boş. Sipariş verilemez.");
		}

		for (BasketItem bi : activeItems) {
			Product product = bi.getProduct();

			int availableStock = product.getStokAdeti() - product.getReservedStock();

			if (bi.getAdet() > availableStock) {
				throw new BusinessException(product.getIsim() + " stok yetersiz. Mevcut: " + availableStock);
			}

			product.setReservedStock(product.getReservedStock() + bi.getAdet());
		}

		Order order = new Order();
		order.setUser(user); 
		order.setTeslimatAdresi(address);
		order.setSiparisdurumu(OrderStatus.PENDING);
		order.setContractsAccepted(true);
		if (request.getContractsAccepted()) {
		    order.setContractsAcceptedAt(ZonedDateTime.now(ZoneId.of("Europe/Istanbul")).toLocalDateTime());
		}
		order.setClientIp(clientIp);
		List<OrderItem> orderItems = new ArrayList<>();
		BigDecimal toplamTutar = BigDecimal.ZERO;

		for (BasketItem bi : activeItems) {
			OrderItem oi = new OrderItem();
			oi.setAdet(bi.getAdet());
			oi.setEklenmetarihi(bi.getOlusturmatarihi());
			oi.setIndirim(bi.getIndirim());
			oi.setOrder(order);
			oi.setProduct(bi.getProduct());

			String imageUrl = null;
			List<String> images = bi.getProduct().getResimler();
			if (images != null && !images.isEmpty()) {
				imageUrl = images.get(0);
			}
			oi.setImageurl(imageUrl);

			BigDecimal itemTotal = bi.getFiyat().multiply(BigDecimal.valueOf(bi.getAdet()));

			oi.setToplamfiyat(itemTotal);
			toplamTutar = toplamTutar.add(itemTotal);
			orderItems.add(oi);
		}
		

		order.setItemlist(orderItems);
		order.setToplamtutar(toplamTutar);

		orderRepository.save(order);
		paymentAuditLogger.log(PaymentEvent.CONTRACT_ACCEPTED, order.getId(), user.getId(), "contractsAccepted=true", clientIp, userAgent);

		for (BasketItem bi : activeItems) {
			bi.setActive(false);
		}
		basketRepository.save(basket);
		

	
		String fakepaymentlink = "https://fakebank.com/pay?orderId=" + order.getId() + "&amount="
				+ toplamTutar.toPlainString();
		paymentAuditLogger.log(PaymentEvent.PAYMENT_REDIRECTED, order.getId(), user.getId(),
				"provider=FAKEBANK | linkGenerated=true",clientIp,userAgent);

		order.setPaymentLink(fakepaymentlink);
		order.setPaymentProvider("FAKEBANK");
		orderRepository.save(order);

		//mailService.sendCustomMail(user.getEmail(), "Ödeme Linki",
				//"Siparişiniz oluşturuldu! Ödeme için link: " + fakepaymentlink);

		return fakepaymentlink;
	}

	@Override
	@Transactional
	public void handlePaymentCallback(Long orderId, boolean success, String paymentId) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Sipariş bulunamadı"));

		if (order.getSiparisdurumu() == OrderStatus.PAID)
			return;

		if (success) {
			order.setSiparisdurumu(OrderStatus.PAID);
			order.setBankPaymentId(paymentId);
			

			for (OrderItem item : order.getItemlist()) {
				Product product = item.getProduct();
				int yeniStok = product.getStokAdeti() - item.getAdet();
				if (yeniStok < 0)
					throw new BusinessException(product.getIsim() + " stok yetersiz!");

				product.setStokAdeti(yeniStok);
				product.setReservedStock(product.getReservedStock() - item.getAdet());
			}

			Basket basket = basketRepository.findByUser(order.getUser())
					.orElseThrow(() -> new BusinessException("Sepet bulunamadı"));

			for (BasketItem bi : basket.getBasketItems())
				bi.setActive(false);

			basketRepository.save(basket);

			orderRepository.save(order);

			mailService.sendOrderMails(order);
			

		} else {
			for (OrderItem item : order.getItemlist()) {
				Product product = item.getProduct();
				product.setReservedStock(product.getReservedStock() - item.getAdet());
			}

			order.setSiparisdurumu(OrderStatus.CANCELLED);
			orderRepository.save(order);
		}
	}

	@Override
	public List<Order> findOrderitem() {
		User user = getAuthenticatedUser();
		return orderRepository.findByUser(user);
	}

	private User getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
			throw new BusinessException("Lütfen giriş yapınız.");
		}

		Object principal = auth.getPrincipal();
		String email;

		if (principal instanceof User)
			email = ((User) principal).getEmail();
		else if (principal instanceof UserDetails)
			email = ((UserDetails) principal).getUsername();
		else
			throw new BusinessException("Bilinmeyen principal tipi");

		return userService.getUserbyEmail(email);
	}

	@Override
	public Long getLastCreatedOrderIdForUser(User user) {
		return orderRepository.findTopByUserIdOrderByOlusturmatarihiDesc(user.getId()).map(Order::getId)
				.orElseThrow(() -> new BusinessException("Sipariş bulunamadı"));
	}

	@Override
	@Transactional
	public ResponseEntity<?> changeOrderStatus(Long id, String cargoTrackingNo) {

		Order order = orderRepository.findById(id).orElseThrow(() -> new BusinessException("Sipariş bulunamadı"));

		if (order.getSiparisdurumu() != OrderStatus.PAID) {
			throw new BusinessException("Sadece ödenmiş sipariş kargoya verilebilir");
		}

		order.setSiparisdurumu(OrderStatus.SHIPPED);
		order.setKargotakipno(cargoTrackingNo);
		order.setShippedAt(LocalDateTime.now());

		orderRepository.save(order);

		try {
			String mailBody = mailService.buildShippedMail(order);

			mailService.sendHtmlMail(order.getUser().getEmail(), "📦 Siparişiniz Kargoya Verildi", mailBody);
		}

		catch (Exception e) {
			adminLogger.error("MAIL_SEND_FAILED | orderId={} | userId={}", order.getId(), order.getUser().getId(), e);
		}

		return ResponseEntity.ok("Sipariş kargoya verildi");
	}

	private DtoOrderItem toItemDto(OrderItem item) {
		DtoOrderItem dto = new DtoOrderItem();

		dto.setÜrünismi(item.getProduct().getIsim());
		dto.setAdet(item.getAdet());
		dto.setFiyat(item.getProduct().getFiyat());
		dto.setIndirim(item.getIndirim());

		dto.setImageUrl(item.getImageurl());

		return dto;
	}

	@Override
	public List<Order> getallorders() {
		return orderRepository.findBySiparisdurumu(OrderStatus.PAID);

	}

	@Override
	public Order findOrderById(Long orderId) {
		return orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Sipariş bulunamadı"));
	}

	private String getClientIp(HttpServletRequest request) {
		String xf = request.getHeader("X-Forwarded-For");
		if (xf != null && !xf.isBlank()) {
			return xf.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}
}
