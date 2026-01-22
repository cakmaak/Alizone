package com.Alizone.Security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HmacUtil {

	public static String hmacSha256(String data, String secret) {
	    try {
	        Mac mac = Mac.getInstance("HmacSHA256");
	        SecretKeySpec secretKey =
	                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	        mac.init(secretKey);
	        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

	        return Base64.getEncoder().encodeToString(rawHmac).trim();

	    } catch (Exception e) {
	        throw new RuntimeException("HMAC üretilemedi", e);
	    }
	}
}