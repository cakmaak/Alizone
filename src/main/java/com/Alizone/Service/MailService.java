package com.Alizone.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

	@Value("${selzy.api.key}")
	private String selzyApiKey;

	@Value("${mail.from}")
	private String mailFrom;
	

	

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	/*
	 * ------------------------------------------------- ESKİ MAİLLER (KALSIN)
	 * --------------------------------------------------
	 */

	public void sendwelcomemail(User user) {

	    String subject = "Alizone Klima’ya Hoşgeldiniz 🌬️";
	    String body =
	            "Merhaba " + user.getIsim() +
	            "\n\nAlizone Klima ailesine hoşgeldiniz.";

	    sendViaSelzy(user.getEmail(), subject, body);
	}

	public void sendResetPasswordEmail(User user, String token) {

	    String resetLink = "https://alizone-ecommerce.vercel.app/reset-password?token=" + token;

	    String subject = "Şifre Sıfırlama";
	    String body =
	            "Şifrenizi sıfırlamak için linke tıklayın:\n" +
	            resetLink +
	            "\n\nBu link 15 dakika geçerlidir.";

	    sendViaSelzy(user.getEmail(), subject, body);
	}
	private void sendViaSelzy(String to, String subject, String body) {

	    String url = "https://api.selzy.com/en/api/sendEmail";

	    Map<String, Object> payload = new HashMap<>();
	    payload.put("format", "json");
	    payload.put("api_key", selzyApiKey);
	    payload.put("from", mailFrom);
	    payload.put("to", to);
	    payload.put("subject", subject);
	    payload.put("body", body);

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setAcceptCharset(java.util.List.of(StandardCharsets.UTF_8));

	    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

	    try {
	        new RestTemplate().postForEntity(url, request, String.class);
	    } catch (Exception e) {
	        log.error("SELZY_MAIL_FAILED to={} subject={}", to, subject, e);
	    }
	}

	/*
	 * ------------------------------------------------- HTML MAIL GÖNDERİM
	 * --------------------------------------------------
	 */

	public void sendHtmlMail(String to, String subject, String html) {

	    String url = "https://api.selzy.com/en/api/sendEmail";

	    Map<String, Object> payload = new HashMap<>();
	    payload.put("format", "json");
	    payload.put("api_key", selzyApiKey);
	    payload.put("from", mailFrom);
	    payload.put("to", to);
	    payload.put("subject", subject);
	    payload.put("body", html);

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setAcceptCharset(java.util.List.of(StandardCharsets.UTF_8));

	    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
	        log.info("Selzy mail gönderildi: {}", response.getBody());
	    } catch (Exception e) {
	        log.error("MAIL_SEND_FAILED | to={} subject={}", to, subject, e);
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

	public void sendOrderMails(Order order)  {

		// MÜŞTERİ
		sendHtmlMail(order.getUser().getEmail(), "🛒 Siparişiniz Alındı", buildCustomerOrderMail(order));

		// ADMIN
		sendHtmlMail(adminMail, "📦 Yeni Sipariş Geldi", buildAdminOrderMail(order));
	}

	public void sendCustomMail(String to, String subject, String body) {
	    sendViaSelzy(to, subject, body);
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
