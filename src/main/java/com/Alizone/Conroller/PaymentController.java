package com.Alizone.Conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Alizone.Dto.DtoPaymentResponse;
import com.Alizone.Dto.Halk3DRequestDto;
import com.Alizone.Service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	
    @PostMapping("/start/{orderId}")
    public ResponseEntity<Halk3DRequestDto> startPayment(@PathVariable Long orderId) throws Exception {
        return ResponseEntity.ok(paymentService.startHalkPayment(orderId));
    }
}
