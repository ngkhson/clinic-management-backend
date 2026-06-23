package com.clinic.booking.config;

import org.springframework.context.annotation.Configuration;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Configuration
public class VNPAYConfig {

    // --- THÔNG SỐ SANDBOX TỪ VNPAY (Bạn có thể thay bằng key thật sau này) ---
    public static final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String vnp_Returnurl = "http://localhost:5173/payment-result"; // Trang React sẽ hứng kết quả
    public static final String vnp_TmnCode = "QTT2026M"; // Mã Website (Dummy)
    public static final String vnp_HashSecret = "1234567890QWERTYUIOPASDFGHJKLZXC"; // Chuỗi bí mật (Dummy)
    public static final String vnp_apiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    // Hàm tạo chuỗi hash bảo mật (HMAC SHA512) theo chuẩn của VNPAY
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    // Hàm sinh mã giao dịch ngẫu nhiên
    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}