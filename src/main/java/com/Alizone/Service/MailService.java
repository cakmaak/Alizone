package com.Alizone.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.enabled:false}")
    private boolean mailEnabled;

    public void sendHtmlMail(String to, String subject, String html) {
        if(!mailEnabled) {
            System.out.println("[SIMULATED MAIL] To: " + to + ", Subject: " + subject);
            System.out.println("HTML: " + html);
            return;
        }

        // Buraya deploy’da gerçek mail servisi entegre edilecek
        System.out.println("[MAIL SERVICE ENABLED] Mail would be sent here");
    }
}