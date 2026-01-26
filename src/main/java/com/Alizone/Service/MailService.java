package com.Alizone.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.message.SimpleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.Alizone.Entity.Address;
import com.Alizone.Entity.Order;
import com.Alizone.Entity.OrderItem;
import com.Alizone.Entity.User;
import com.Alizone.Enum.OrderStatus;

@Service
public class MailService {

	@Value("${mail.admin}")
	private String adminMail;

	@Value("${resend.api.key}")
	private String resendapikey;

	@Value("${mail.from}")
	private String mailFrom;
	

	

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	/*
	 * ------------------------------------------------- ESKİ MAİLLER (KALSIN)
	 * --------------------------------------------------
	 */

	public void sendHtmlMail(String to, String subject, String body) {
        
        System.out.println("MAIL SEND SIMULATED -> To: " + to + ", Subject: " + subject);
    }
}