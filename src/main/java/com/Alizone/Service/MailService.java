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
        StringBuilder sb = new StringBuilder();

        sb.append("""
            <div style="font-family:Arial;max-width:600px;margin:auto">
            <h2>🛒 Siparişiniz Alındı</h2>
            <p>Sipariş No: <b>#%d</b></p>
            <hr>
        """.formatted(order.getId()));

        for (OrderItem item : order.getItemlist()) {
            sb.append("""
                <p>
                    <b>%s</b><br>
                    Adet: %d<br>
                    Tutar: %.2f ₺
                </p>
            """.formatted(
                    item.getProduct().getIsim(),
                    item.getAdet(),
                    item.getToplamfiyat()
            ));
        }

        sb.append("""
            <hr>
            <p>
                %s<br>
                %s / %s<br>
                %s
            </p>
            </div>
        """.formatted(
                a.getAdresSatir1(),
                a.getIlce(),
                a.getSehir(),
                a.getTelefon()
        ));

        return sb.toString();
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
        return """
            <div style="font-family:Arial;max-width:600px;margin:auto">
            <h2>⚠️ Sipariş İptali</h2>
            <p>Sipariş: #%d</p>
            <p>Durum: %s</p>
            </div>
        """.formatted(order.getId(), order.getSiparisdurumu());
    }
	

}
