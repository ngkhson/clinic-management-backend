package com.clinic.booking.service;

import com.clinic.booking.config.VNPAYConfig;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentService {
    private final VNPAYConfig vnpayConfig;

    public PaymentService(VNPAYConfig vnpayConfig) {
        this.vnpayConfig = vnpayConfig;
    }

    public String createPaymentUrl(String targetType, Long targetId, double amount, String ipAddress) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        
        // Tiền tố xác định loại hóa đơn: INV- (Khám bệnh) hoặc RET- (Bán lẻ)
        String prefix = "INVOICE".equalsIgnoreCase(targetType) ? "INV" : "RET";
        String vnp_TxnRef = prefix + "-" + targetId + "-" + VNPAYConfig.getRandomNumber(4); 
        
        String vnp_IpAddr = ipAddress;
        String vnp_TmnCode = vnpayConfig.getVnp_TmnCode();

        // VNPAY tính tiền bằng VNĐ nhân 100 (VD: 100,000 VNĐ -> 10000000)
        long amountInVND = (long) (amount * 100);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVND));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        String orderInfo = "INVOICE".equalsIgnoreCase(targetType) 
                ? "Thanh toan hoa don kham benh: " + targetId 
                : "Thanh toan hoa don mua thuoc: " + targetId;
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getVnp_Returnurl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Hết hạn sau 15 phút
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build URL
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPAYConfig.hmacSHA512(vnpayConfig.getVnp_HashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;
    }
}