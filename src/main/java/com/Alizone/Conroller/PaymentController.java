package com.Alizone.Conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Alizone.Service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/halk/link/{orderId}")
    public ResponseEntity<Void> createRealHalkLink(@PathVariable Long orderId) {

        // 1️⃣ Order alıyoruz
        String paymentUrl = paymentService.createRealPurchaseLink(orderId);

        return ResponseEntity
                .status(302)
                .header("Location", paymentUrl)
                .build();
    }
}