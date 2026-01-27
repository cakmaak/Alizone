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

        sendHtmlMail(
                adminMail,
                "⚠️ Sipariş İptal Edildi - #" + order.getId(),
                buildAdminOrderCancelledMail(order)
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
            itemsHtml.append("""
                <tr>
                  <td style="padding:10px">
                    <img src="%s" width="80" style="border-radius:8px"/>
                  </td>
                  <td>
                    <b>%s</b><br/>
                    Adet: %d
                  </td>
                  <td align="right">
                    %.2f ₺
                  </td>
                </tr>
            """.formatted(
                item.getProduct().getResimler(),   // 🔥 önemli
                item.getProduct().getIsim(),
                item.getAdet(),
                item.getToplamfiyat()
            ));
        }

        return """
        <div style="font-family:Arial;max-width:600px;margin:auto">
          <h2>🛒 Siparişiniz Alındı</h2>
          <p>Sipariş No: <b>#%d</b></p>

          <table width="100%%" style="border-collapse:collapse">
            %s
          </table>

          <hr/>
          <p><b>Toplam:</b> %.2f ₺</p>

          <h4>📍 Teslimat Adresi</h4>
          <p>
            %s<br/>
            %s / %s<br/>
            %s
          </p>

          <p style="color:#888">Alizone Klima 💙</p>
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

        return """
            <div style="font-family:Arial;max-width:600px;margin:auto">
            <h2>📦 Yeni Sipariş</h2>

            <p><b>Sipariş:</b> #%d</p>
            <p><b>Müşteri:</b> %s (%s)</p>

            <hr>

            <p>
                %s<br>
                %s / %s<br>
                %s
            </p>

            <p><b>Toplam:</b> %.2f ₺</p>
            </div>
        """.formatted(
                order.getId(),
                order.getUser().getIsim(),
                order.getUser().getEmail(),
                a.getAdresSatir1(),
                a.getIlce(),
                a.getSehir(),
                a.getTelefon(),
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
            "https://alizone-ecommerce.vercel.app/reset-password?token=" + token;

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
