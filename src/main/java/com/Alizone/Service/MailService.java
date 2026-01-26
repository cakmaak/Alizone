package com.Alizone.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Value("${mail.from}")
    private String mailFrom;

    public void sendHtmlMail(String to, String subject, String html) {
        // Gerçek mail gönderimi yok, sadece log basılıyor
        System.out.println("MAIL SIMULATED -> To: " + to + ", Subject: " + subject);
        System.out.println("HTML: " + html);
    }
}