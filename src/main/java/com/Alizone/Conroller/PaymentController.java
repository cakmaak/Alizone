package com.Alizone.Conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Alizone.Dto.DtoPaymentResponse;
import com.Alizone.Service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PostMapping("/callback")
	public ResponseEntity<String> paymentCallback(@RequestBody DtoPaymentResponse response) {

		try {
			paymentService.processCallback(response.getOrderId(), response.isSuccess(), response.getTimestamp(),
					response.getPaymentId(),response.getSignature());

			return ResponseEntity.ok("Callback doğrulandı ✅");

		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());

		} catch (Exception e) {
			return ResponseEntity.status(500).body("Callback hatası");
		}
	}
	@GetMapping("/test-sign")
	public String paymenttest(@RequestParam Long orderId, @RequestParam boolean success, @RequestParam long timestamp,@RequestParam String paymentId) {
		return paymentService.testSign(orderId, success, timestamp,paymentId);
	}
}
