package com.Alizone.Service;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.Alizone.Entity.Address;
import com.Alizone.Entity.Order;
import com.Alizone.Entity.OrderItem;
import com.Alizone.Entity.Product;
import com.Alizone.Entity.User;
import com.Alizone.Enum.OrderStatus;

@Service
public class MailService {

    @Value("${mail.admin.address}")
    private String adminMail;

    @Value("${mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${spring.mail.from}")
    private String fromMail;

    private static final String BREVO_URL =
            "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    /* =========================================================
       CORE SEND METHOD (BREVO)
       ========================================================= */
    
    private String safe(String value) {
        return value == null ? "" : value;
    }

    public void sendHtmlMail(String to, String subject, String html) {
    	
    	if (brevoApiKey == null || brevoApiKey.isBlank()) {
    	    log.warn("BREVO_API_KEY missing, mail skipped");
    	    return;
    	}

        if (!mailEnabled) {
            log.info("[MAIL DISABLED] to={} subject={}", to, subject);
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of(
                "name", "Alizone Klima",
                "email", fromMail
        ));
        body.put("to", List.of(Map.of("email", to)));
        body.put("subject", subject);
        body.put("htmlContent", html);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(BREVO_URL, request, String.class);
            log.info("MAIL_SENT | to={}", to);
        } catch (Exception e) {
            log.error("MAIL_SEND_FAILED | to={}", to, e);
        }
    }

    /* =========================================================
       ORDER MAILS
       ========================================================= */

    public void sendOrderMails(Order order) {
        sendHtmlMail(
                order.getUser().getEmail(),
                "🛒 Siparişiniz Alındı",
                buildCustomerOrderMail(order)
        );

        sendHtmlMail(
                adminMail,
                "📦 Yeni Sipariş Geldi - #" + order.getId(),
                buildAdminOrderMail(order)
        );
    }

    public void sendOrderCancelledMails(Order order) {
    	
        sendHtmlMail(
                order.getUser().getEmail(),
                "❌ Siparişiniz İptal Edildi",
                
                buildOrderCancelledMail(order)
        );
        String adminHtml = buildAdminOrderCancelledMail(order)
                + "<br><b>Adres:</b> " + order.getTeslimatAdresi();

        sendHtmlMail(
        		adminMail,
        	    "⚠️ Sipariş İptal Edildi - #" + order.getId(),
        	    adminHtml
        );
    }

    public void sendShippedMail(Order order) {
        sendHtmlMail(
                order.getUser().getEmail(),
                "📦 Siparişiniz Kargoya Verildi",
                buildShippedMail(order)
        );
    }

    /* =========================================================
       HTML BUILDERS
       ========================================================= */

    public String buildCustomerOrderMail(Order order) {

        Address a = order.getTeslimatAdresi();
        StringBuilder itemsHtml = new StringBuilder();

        
        for (OrderItem item : order.getItemlist()) {
        	String imageUrl = item.getProduct().getResimler().isEmpty()
        	        ? "https://alizone.com/no-image.png"
        	        : item.getProduct().getResimler().get(0);
        	itemsHtml.append("""
        			<tr>
        			  <td style="padding:20px 0">
        			    <table width="100%%" style="
        			        border:1px solid #eee;
        			        border-radius:14px;
        			        background:#ffffff;
        			        box-shadow:0 6px 18px rgba(0,0,0,0.08);
        			        overflow:hidden;
        			    ">
        			      <tr>
        			        <td style="padding:15px">
        			          <img src="%s"
        			               style="
        			                 width:100%%;
        			                 max-height:280px;
        			                 object-fit:contain;
        			                 border-radius:12px;
        			                 background:#f7f7f7;
        			               "/>
        			        </td>
        			      </tr>
        			      <tr>
        			        <td style="padding:15px">
        			          <h3 style="margin:0;color:#0f172a">%s</h3>
        			          <p style="margin:6px 0;color:#475569">
        			            Adet: <b>%d</b>
        			          </p>
        			          <p style="
        			            margin:10px 0 0;
        			            font-size:18px;
        			            color:#16a34a;
        			            font-weight:bold;
        			          ">
        			            %.2f ₺
        			          </p>
        			        </td>
        			      </tr>
        			    </table>
        			  </td>
        			</tr>
        			""".formatted(
        			  imageUrl,
        			  item.getProduct().getIsim(),
        			  item.getAdet(),
        			  item.getToplamfiyat()
        			));
        }

        return """
        		<div style="font-family:Arial;max-width:600px;margin:auto">

        		<!-- HEADER -->
        		<div style="
        		  background:linear-gradient(135deg,#2563eb,#22c55e);
        		  padding:25px;
        		  border-radius:18px;
        		  color:white;
        		  text-align:center;
        		  margin-bottom:25px;
        		">
        		  <h1 style="margin:0">🛒 Alizone Klima</h1>
        		  <p style="margin:8px 0 0;font-size:14px">
        		    Premium Klima & Soğutma Sistemleri
        		  </p>
        		</div>

        		<h2>🛒 Siparişiniz Alındı</h2>
        		<p>Sipariş No: <b>#%d</b></p>

        		<table width="100%%">
        		  %s
        		</table>

        		<hr/>

        		<p style="font-size:18px">
        		  <b>Toplam:</b> %.2f ₺
        		</p>

        		<h4>📍 Teslimat Adresi</h4>
        		<p>
        		  %s<br/>
        		  %s / %s<br/>
        		  %s
        		</p>

        		<!-- FOOTER -->
        		<hr style="margin:30px 0"/>

        		<p style="text-align:center;color:#64748b;font-size:13px">
        		  🌐 <a href="https://alizoneklima.com">www.alizoneklima.com</a><br/>
        		  📞 0554 230 9563<br/>
        		  📍 Ankara Bosch Sakura Klima Yetkili Bayi
        		</p>

        		</div>
        		""".formatted(
            order.getId(),
            itemsHtml,
            order.getToplamtutar(),
            a.getAdresSatir1(),
            a.getIlce(),
            a.getSehir(),
            a.getTelefon()
        );
    }

    public String buildAdminOrderMail(Order order) {

        Address a = order.getTeslimatAdresi();

        
        String addressHtml = """
            <h3>📍 Teslimat Bilgileri</h3>
            <p>
              <b>Alıcı:</b> %s<br/>
              %s %s<br/>
              %s / %s<br/>
              %s<br/>
              <b>Tel:</b> %s
            </p>

            <hr/>

            <h3>🧾 Fatura Bilgileri</h3>
            <p>
              <b>Fatura Tipi:</b> %s<br/>
              <b>Ad Soyad:</b> %s<br/>
              <b>TC Kimlik:</b> %s<br/>
              <b>Firma:</b> %s<br/>
              <b>Vergi No:</b> %s<br/>
              <b>Vergi Dairesi:</b> %s
            </p>
        """.formatted(
                safe(a.getAliciAdiSoyadi()),
                safe(a.getAdresSatir1()),
                safe(a.getAdresSatir2()),
                safe(a.getIlce()),
                safe(a.getSehir()),
                safe(a.getPostaKodu()),
                safe(a.getTelefon()),

                safe(a.getFaturaTipi()),
                safe(a.getFaturaAdiSoyadi()),
                safe(a.getTcKimlikNo()),
                safe(a.getFirmaAdi()),
                safe(a.getVergiNo()),
                safe(a.getVergiDairesi())
        );

        
        StringBuilder itemsHtml = new StringBuilder("<h3>🛒 Sipariş Ürünleri</h3>");
        for (OrderItem item : order.getItemlist()) {
            String imageUrl = item.getProduct().getResimler().isEmpty()
                    ? "https://alizone.com/no-image.png"
                    : item.getProduct().getResimler().get(0);

            itemsHtml.append("""
                <div style="display:flex; align-items:center; gap:10px; margin-bottom:10px; padding:10px; border:1px solid #eee; border-radius:10px; background:#fafafa">
                    <img src="%s" alt="%s" style="width:60px; height:60px; object-fit:contain; border-radius:6px"/>
                    <div style="flex:1">
                        <b>%s</b><br/>
                        Adet: %d<br/>
                        Fiyat: %.2f ₺
                    </div>
                </div>
            """.formatted(
                    imageUrl,
                    safe(item.getProduct().getIsim()),
                    safe(item.getProduct().getIsim()),
                    item.getAdet(),
                    item.getToplamfiyat()
            ));
        }

        // Final HTML
        return """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto">
                <h2>📦 Yeni Sipariş Geldi</h2>

                %s

                <hr/>

                %s

                <hr/>

                <p style="font-size:18px">
                    <b>🧾 Sepet Toplamı:</b> %.2f ₺
                </p>

            </div>
        """.formatted(
            addressHtml,
            itemsHtml.toString(),
            order.getToplamtutar()
        );
    }


    public String buildShippedMail(Order order) {
        return """
            <div style="font-family:Arial;max-width:600px;margin:auto">
            <h2>📦 Siparişiniz Kargoya Verildi</h2>

            <p>Sipariş No: <b>#%d</b></p>
            <p>Kargo Takip No: <b>%s</b></p>
            </div>
        """.formatted(
                order.getId(),
                order.getKargotakipno()
        );
    }

    public String buildOrderCancelledMail(Order order) {

        String refundText =
                order.getSiparisdurumu() == OrderStatus.REFUND_PENDING
                        ? "İade süreci başlatıldı."
                        : "Ödeme alınmadı.";

        return """
            <div style="font-family:Arial;max-width:600px;margin:auto">
            <h2>❌ Sipariş İptal Edildi</h2>
            <p>#%d numaralı sipariş iptal edildi.</p>
            <p>%s</p>
            </div>
        """.formatted(order.getId(), refundText);
    }

    public String buildAdminOrderCancelledMail(Order order) {

        StringBuilder itemsHtml = new StringBuilder();

        for (OrderItem item : order.getItemlist()) {
            itemsHtml.append("""
                <tr>
                    <td style="padding:8px 0">
                        <b>%s</b><br/>
                        Adet: %d
                    </td>
                    <td align="right">
                        %.2f ₺
                    </td>
                </tr>
            """.formatted(
                item.getProduct().getIsim(),
                item.getAdet(),
                item.getToplamfiyat()
            ));
        }

        return """
        <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto">
            <h2 style="color:#d9534f">⚠️ Sipariş İptali</h2>

            <p><b>Sipariş No:</b> #%d</p>
            <p><b>Durum:</b> %s</p>

            <hr/>

            <p><b>Müşteri:</b><br/>
               %s<br/>
               %s
            </p>

            <table width="100%%" style="border-collapse:collapse">
                %s
            </table>

            <hr/>

            <p style="font-size:16px">
                <b>Toplam Tutar:</b> %.2f ₺
            </p>

            <p style="color:#888;font-size:12px">
                Bu sipariş müşteri tarafından iptal edilmiştir.
            </p>
        </div>
        """.formatted(
            order.getId(),
            order.getSiparisdurumu(),
            order.getUser().getIsim(),
            order.getUser().getEmail(),
            itemsHtml,
            order.getToplamtutar()
        );
    }
    public void sendCustomMail(String to, String subject, String content) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> body = Map.of(
            "sender", Map.of(
                "name", "Alizone",
                "email", adminMail
            ),
            "to", List.of(
                Map.of("email", to)
            ),
            "subject", subject,
            "htmlContent", "<p>" + content + "</p>"
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        restTemplate.postForEntity(BREVO_URL, entity, String.class);
    }
    public void sendwelcomemail(User user) {

        String html = """
            <div style="font-family:Arial,sans-serif">
                <h2>🎉 Alizone’a Hoş Geldiniz!</h2>
                <p>Merhaba <b>%s</b>,</p>
                <p>Hesabınız başarıyla oluşturuldu.</p>
                <p>Artık Alizone üzerinden sipariş verebilir,
                kampanyaları takip edebilirsiniz.</p>
                <br/>
                <p>💙 İyi alışverişler dileriz</p>
                <p><b>Alizone Ekibi</b></p>
            </div>
        """.formatted(user.getIsim());

        sendHtmlMail(
            user.getEmail(),
            "🎉 Alizone’a Hoş Geldiniz",
            html
        );
    }
    public void sendResetPasswordEmail(User user, String token) {

    	String resetLink =
    			  "https://alizoneecommerce.onrender.com/#/reset-password?token=" + token;

        String html = """
            <h2>🔐 Şifre Sıfırlama</h2>
            <p>Merhaba <b>%s</b>,</p>
            <p>Şifrenizi sıfırlamak için aşağıdaki linke tıklayın:</p>
            <a href="%s">Şifremi Sıfırla</a>
            <br/><br/>
            <p>⏰ Bu link 15 dakika geçerlidir.</p>
        """.formatted(user.getIsim(), resetLink);

        sendHtmlMail(
            user.getEmail(),
            "🔐 Şifre Sıfırlama",
            html
        );
    }

}
