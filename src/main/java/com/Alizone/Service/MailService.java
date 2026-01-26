package com.Alizone.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.Alizone.Entity.Address;
import com.Alizone.Entity.Order;
import com.Alizone.Entity.OrderItem;
import com.Alizone.Entity.User;
import com.Alizone.Enum.OrderStatus;

@Service
public class MailService {

	@Value("${mail.admin.address}")
	private String adminMail;

	@Value("${spring.mail.from}")
	private String MAIL_FROM;

	@Value("${mail.enabled:true}")
	private boolean mailEnabled;

	private final JavaMailSender mailSender;

	@Autowired
	public MailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	/*
	 * ------------------------------------------------- ESKİ MAİLLER (KALSIN)
	 * --------------------------------------------------
	 */
	
	public void sendwelcomemail(User user) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Alizone Klima’ya Hoşgeldiniz 🌬️");
		message.setText("Merhaba " + user.getIsim() + "\n\nAlizone Klima ailesine hoşgeldiniz.");
		mailSender.send(message);
	}

	public void sendResetPasswordEmail(User user, String token) {

		String resetLink = "http://localhost:5173/reset-password?token=" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Şifre Sıfırlama");
		message.setText(
				"Şifrenizi sıfırlamak için linke tıklayın:\n" + resetLink + "\n\nBu link 15 dakika geçerlidir.");

		mailSender.send(message);
	}
	public void sendSimpleMail(String to, String subject, String text) {
        if (!mailEnabled) {
            System.out.println("[SIMULATED MAIL] To: " + to + ", Subject: " + subject);
            System.out.println("Text: " + text);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(MAIL_FROM);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            mailSender.send(message);
            System.out.println("Mail gönderildi: " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Mail gönderilemedi: " + e.getMessage());
        }
    }

	/*
	 * ------------------------------------------------- HTML MAIL GÖNDERİM
	 * --------------------------------------------------
	 */

	public void sendHtmlMail(String to, String subject, String html) {
        if (!mailEnabled) {
            System.out.println("[SIMULATED MAIL] To: " + to + ", Subject: " + subject);
            System.out.println("HTML: " + html);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(MAIL_FROM);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // HTML true

            mailSender.send(message);
            System.out.println("Mail gönderildi: " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Mail gönderilemedi: " + e.getMessage());
        }
    }

	/*
	 * ------------------------------------------------- MÜŞTERİ MAİLİ
	 * --------------------------------------------------
	 */

	public String buildCustomerOrderMail(Order order) {

		Address a = order.getTeslimatAdresi();
		StringBuilder sb = new StringBuilder();

		sb.append("""
				    <div style="font-family:Arial;max-width:600px;margin:auto">
				        <h2 style="color:#2c3e50">🛒 Siparişiniz Alındı</h2>
				        <p>Sipariş Numaranız: <b>#%d</b></p>
				        <hr>
				""".formatted(order.getId()));

		for (OrderItem item : order.getItemlist()) {
			sb.append("""
					    <div style="display:flex;margin-bottom:15px">
					        <img src="%s" width="120"
					             style="margin-right:15px;border-radius:8px"/>
					        <div>
					            <b>%s</b><br>
					            Adet: %d<br>
					            Ürün Tutarı: %.2f ₺
					        </div>
					    </div>
					""".formatted(item.getProduct().getResimler().get(0), item.getProduct().getIsim(), item.getAdet(),
					item.getToplamfiyat()));
		}

		sb.append("""
				    <hr>
				    <h4>📍 Teslimat Adresi</h4>
				    <p>
				        %s<br>
				        %s<br>
				        %s<br>
				        %s / %s<br>
				        %s<br>
				        %s<br>
				        %s
				    </p>
				""".formatted(a.getAliciAdiSoyadi(), a.getAdresSatir1(),
				a.getAdresSatir2() == null ? "" : a.getAdresSatir2(), a.getIlce(), a.getSehir(), a.getPostaKodu(),
				a.getUlke(), a.getTelefon()));

		// FATURA BİLGİLERİ (varsa)
		if (a.getFaturaTipi() != null) {
			sb.append("""
					    <hr>
					    <h4>💼 Fatura Bilgileri</h4>
					    <p>
					        Fatura Tipi: %s<br>
					        Adı Soyadı: %s<br>
					        TC Kimlik No: %s<br>
					        Firma Adı: %s<br>
					        Vergi No: %s<br>
					        Vergi Dairesi: %s
					    </p>
					""".formatted(a.getFaturaTipi(), a.getFaturaAdiSoyadi() == null ? "" : a.getFaturaAdiSoyadi(),
					a.getTcKimlikNo() == null ? "" : a.getTcKimlikNo(), a.getFirmaAdi() == null ? "" : a.getFirmaAdi(),
					a.getVergiNo() == null ? "" : a.getVergiNo(),
					a.getVergiDairesi() == null ? "" : a.getVergiDairesi()));
		}

		sb.append("""
				    <p style="color:#888;font-size:13px">
				        Siparişiniz hazırlanıyor, kargoya verildiğinde bilgilendirileceksiniz.
				    </p>
				    </div>
				""");

		return sb.toString();
	}

	/*
	 * ------------------------------------------------- ADMIN MAİLİ
	 * --------------------------------------------------
	 */

	public String buildAdminOrderMail(Order order) {

		Address a = order.getTeslimatAdresi();
		StringBuilder sb = new StringBuilder();

		sb.append("""
				    <div style="font-family:Arial;max-width:700px;margin:auto">
				        <h2 style="color:#c0392b">📦 Yeni Sipariş Geldi</h2>

				        <p><b>Sipariş No:</b> #%d</p>
				        <p><b>Müşteri:</b> %s (%s)</p>

				        <hr>

				        <h3>📍 Teslimat Adresi</h3>
				        <p style="background:#f7f7f7;padding:10px;border-radius:6px">
				            %s<br>
				            %s<br>
				            %s<br>
				            %s / %s<br>
				            %s<br>
				            %s<br>
				            %s
				        </p>
				""".formatted(order.getId(), order.getUser().getIsim(), order.getUser().getEmail(),
				a.getAliciAdiSoyadi(), a.getAdresSatir1(), a.getAdresSatir2() == null ? "" : a.getAdresSatir2(),
				a.getIlce(), a.getSehir(), a.getPostaKodu(), a.getUlke(), a.getTelefon()));

		// FATURA BİLGİLERİ (opsiyonel)
		if (a.getFaturaTipi() != null) {
			sb.append("""
					    <hr>
					    <h4>💼 Fatura Bilgileri</h4>
					    <p>
					        Fatura Tipi: %s<br>
					        Adı Soyadı: %s<br>
					        TC Kimlik No: %s<br>
					        Firma Adı: %s<br>
					        Vergi No: %s<br>
					        Vergi Dairesi: %s
					    </p>
					""".formatted(a.getFaturaTipi(), a.getFaturaAdiSoyadi() == null ? "" : a.getFaturaAdiSoyadi(),
					a.getTcKimlikNo() == null ? "" : a.getTcKimlikNo(), a.getFirmaAdi() == null ? "" : a.getFirmaAdi(),
					a.getVergiNo() == null ? "" : a.getVergiNo(),
					a.getVergiDairesi() == null ? "" : a.getVergiDairesi()));
		}

		// Ürünler
		sb.append("<h3>🧾 Ürünler</h3>");
		for (OrderItem item : order.getItemlist()) {
			sb.append("""
					    <div style="display:flex;margin-bottom:15px">
					        <img src="%s" width="100"
					             style="margin-right:15px;border-radius:6px"/>
					        <div>
					            <b>%s</b><br>
					            Adet: %d<br>
					            Kalan Stok: <b style="color:red">%d</b><br>
					            Ürün Toplam: %.2f ₺
					        </div>
					    </div>
					""".formatted(item.getProduct().getResimler().get(0), item.getProduct().getIsim(), item.getAdet(),
					item.getProduct().getStokAdeti(), item.getToplamfiyat()));
		}

		sb.append("""
				    <hr>
				    <h3>💰 Sipariş Toplamı: %.2f ₺</h3>

				    <p style="font-size:13px;color:#555">
				        ⚠️ Bu sipariş kargoya verilmek üzere hazırlanmalıdır.
				    </p>
				    </div>
				""".formatted(order.getToplamtutar()));

		return sb.toString();
	}

	/*
	 * ------------------------------------------------- DIŞARIDAN KULLANIM
	 * --------------------------------------------------
	 */

	public void sendOrderMails(Order order) throws MessagingException {

		// MÜŞTERİ
		sendHtmlMail(order.getUser().getEmail(), "🛒 Siparişiniz Alındı", buildCustomerOrderMail(order));

		// ADMIN
		sendHtmlMail(adminMail, "📦 Yeni Sipariş Geldi", buildAdminOrderMail(order));
	}

	public void sendCustomMail(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);
	}

	public String buildShippedMail(Order order) {

		Address a = order.getTeslimatAdresi();
		StringBuilder sb = new StringBuilder();

		sb.append("""
				    <div style="font-family:Arial;max-width:600px;margin:auto">
				        <h2 style="color:#27ae60">📦 Siparişiniz Kargoya Verildi</h2>
				        <p>Sipariş Numaranız: <b>#%d</b></p>
				        <p>
				            Kargo Takip No: <b>%s</b><br>
				            Kargoya Verilme Tarihi: %s
				        </p>
				        <hr>
				""".formatted(order.getId(), order.getKargotakipno(), order.getShippedAt().toLocalDate()));

		for (OrderItem item : order.getItemlist()) {
			sb.append("""
					    <div style="display:flex;margin-bottom:15px">
					        <img src="%s" width="120"
					             style="margin-right:15px;border-radius:8px"/>
					        <div>
					            <b>%s</b><br>
					            Adet: %d<br>
					            Ürün Tutarı: %.2f ₺
					        </div>
					    </div>
					""".formatted(item.getProduct().getResimler().get(0), item.getProduct().getIsim(), item.getAdet(),
					item.getToplamfiyat()));
		}

		sb.append("""
				    <hr>
				    <h4>📍 Teslimat Adresi</h4>
				    <p>
				        %s<br>
				        %s<br>
				        %s<br>
				        %s / %s<br>
				        %s<br>
				        %s<br>
				        %s
				    </p>
				    </div>
				""".formatted(a.getAliciAdiSoyadi(), a.getAdresSatir1(),
				a.getAdresSatir2() == null ? "" : a.getAdresSatir2(), a.getIlce(), a.getSehir(), a.getPostaKodu(),
				a.getUlke(), a.getTelefon()));

		return sb.toString();
	}

	public String buildOrderCancelledMail(Order order) {

		String refundText = order.getSiparisdurumu() == OrderStatus.REFUND_PENDING
				? "💳 Ödeme yaptığınız için iade süreci başlatılmıştır. 3–5 iş günü içinde tamamlanır."
				: "💰 Ödeme alınmadığı için herhangi bir iade işlemi yapılmayacaktır.";

		StringBuilder sb = new StringBuilder();

		sb.append("""
				    <div style="font-family:Arial;max-width:600px;margin:auto">
				        <h2 style="color:#e74c3c">❌ Siparişiniz İptal Edildi</h2>

				        <p>
				            <b>#%d</b> numaralı siparişiniz iptal edilmiştir.
				        </p>

				        <hr>
				""".formatted(order.getId()));

		// 🔥 ÜRÜNLER + TOPLAM
		sb.append(buildOrderItemsHtml(order));

		sb.append("""
				    <hr>
				    <p style="color:#555">%s</p>

				    <p style="font-size:13px;color:#888">
				        Herhangi bir sorunuz olursa bizimle iletişime geçebilirsiniz.
				    </p>
				    </div>
				""".formatted(refundText));

		return sb.toString();
	}

	public String buildAdminOrderCancelledMail(Order order) {

		return """
				    <div style="font-family:Arial;max-width:600px;margin:auto">
				        <h2 style="color:#c0392b">⚠️ Sipariş İptal Edildi</h2>

				        <p><b>Sipariş No:</b> #%d</p>
				        <p><b>Müşteri:</b> %s (%s)</p>
				        <p><b>Durum:</b> %s</p>

				        <hr>

				        <p>
				            %s
				        </p>

				        <p style="font-size:13px;color:#555">
				            Admin panelden gerekli işlemleri yapınız.
				        </p>
				    </div>
				""".formatted(order.getId(), order.getUser().getIsim(), order.getUser().getEmail(),
				order.getSiparisdurumu(),
				order.getSiparisdurumu() == OrderStatus.REFUND_PENDING
						? "💰 Bu sipariş için refund işlemi başlatılmalıdır."
						: "Bu sipariş için ödeme alınmamıştır.");
	}

	public void sendOrderCancelledMails(Order order) {

		try {
			// 👤 MÜŞTERİ
			sendHtmlMail(order.getUser().getEmail(), "❌ Siparişiniz İptal Edildi", buildOrderCancelledMail(order));

			// 🛠 ADMIN
			sendHtmlMail(adminMail, "⚠️ Sipariş İptal Edildi - #" + order.getId(), buildAdminOrderCancelledMail(order));

		} catch (Exception e) {
			log.error("MAIL_SEND_FAILED | orderId={}", order.getId(), e);
		}
	}

	private String buildOrderItemsHtml(Order order) {

		StringBuilder sb = new StringBuilder();

		sb.append("<h3>🧾 Sipariş Detayları</h3>");

		for (OrderItem item : order.getItemlist()) {
			sb.append("""
					    <div style="display:flex;margin-bottom:15px">
					        <img src="%s" width="120"
					             style="margin-right:15px;border-radius:8px"/>
					        <div>
					            <b>%s</b><br>
					            Adet: %d<br>
					            Ürün Tutarı: %.2f ₺
					        </div>
					    </div>
					""".formatted(item.getProduct().getResimler().get(0), item.getProduct().getIsim(), item.getAdet(),
					item.getToplamfiyat()));
		}

		sb.append("""
				    <hr>
				    <h3>💰 Toplam Tutar: %.2f ₺</h3>
				""".formatted(order.getToplamtutar()));

		return sb.toString();

	}
	

}
