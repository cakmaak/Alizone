package com.Alizone.Conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Alizone.Service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/halk/fake-link/{orderId}")
    public ResponseEntity<Void> createFakeLink(@PathVariable Long orderId) {

        // createFakePurchaseLink metodunu kullanalım
        String paymentUrl = paymentService.createFakePurchaseLink(orderId);

        return ResponseEntity
                .status(302)
                .header("Location", paymentUrl)
                .build();
    }
}
