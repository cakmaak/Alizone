package Payment;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HalkHashGenerator {

    @Value("${halk.merchant-key}")
    private String merchantKey;

    @Value("${halk.app-secret}")
    private String appSecret;

    public String generateHash(String total,
                               String installment,
                               String currency,
                               String invoiceId) throws Exception {

        String data = total + "|" +
                installment + "|" +
                currency + "|" +
                merchantKey + "|" +
                invoiceId;

        String sha1Input = data + appSecret;

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Bytes = sha1.digest(sha1Input.getBytes(StandardCharsets.UTF_8));

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] sha256Bytes = sha256.digest(sha1Bytes);

        return Base64.getEncoder().encodeToString(sha256Bytes);
    }
    
    public String generateConfirmHash(
            String merchantKey,
            String invoiceId,
            String status
    ) throws Exception {

        String data = merchantKey + "|" + invoiceId + "|" + status;

        String sha1Input = data + appSecret;

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Bytes = sha1.digest(sha1Input.getBytes(StandardCharsets.UTF_8));

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] sha256Bytes = sha256.digest(sha1Bytes);

        return Base64.getEncoder().encodeToString(sha256Bytes);
    }
}