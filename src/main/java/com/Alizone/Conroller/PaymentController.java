package com.Alizone.Conroller;

import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Alizone.Service.HalkPaymentService;
import com.Alizone.Service.HalkTokenService;
import com.Alizone.Service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

  

   @Autowired
   private PaymentService paymentService;
   
   @Autowired
   private HalkTokenService halkTokenService;



    @PostMapping("/halk/link/{orderId}")
    public ResponseEntity<Void> createRealHalkLink(@PathVariable Long orderId) {

        // 1️⃣ Order alıyoruz
        String paymentUrl = paymentService.createRealPurchaseLink(orderId);

        return ResponseEntity
                .status(302)
                .header("Location", paymentUrl)
                .build();
    }
    @GetMapping("/ping-test")
    public String pingTest() {
        try {
            URL url = new URL("https://testapp.halkode.com.tr");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.connect();
            return "Connected";
        } catch (Exception e) {
            return e.toString();
        }
    }
    @GetMapping("/gettoken")
    public String gettoken() {
    	return halkTokenService.getToken();
    	
    }
}