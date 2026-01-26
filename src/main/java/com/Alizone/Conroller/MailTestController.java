package com.Alizone.Conroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Alizone.Service.MailService;

@RestController
@RequestMapping("/test")
public class MailTestController {

    private final MailService mailService;

    public MailTestController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/mail")
    public String sendTestMail() {
        try {
            mailService.sendHtmlMail(
                "kendi_mailin@gmail.com",
                "🔥 Test Mail - Resend",
                "<h2>Knk mail geldi mi?</h2><p>Resend ile test başarılıysa sipariş mailleri de çalışacak.</p>"
            );
            return "Test mail gönderildi";
        } catch (Exception e) {
            return "Mail gönderilemedi: " + e.getMessage();
        }
    }
}