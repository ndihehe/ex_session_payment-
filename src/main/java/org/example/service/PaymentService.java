package org.example.service;

import org.example.model.Order;
import org.example.util.VNPayConfig;
import org.example.util.VNPayUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class PaymentService {

    public static final double USD_TO_VND_RATE = 25000.0;

    public static class PaymentResult {
        private final boolean success;
        private final String orderId;
        private final String transactionNo;
        private final String responseCode;
        private final String message;
        private final long amount;

        public PaymentResult(boolean success, String orderId, String transactionNo, String responseCode, String message, long amount) {
            this.success = success;
            this.orderId = orderId;
            this.transactionNo = transactionNo;
            this.responseCode = responseCode;
            this.message = message;
            this.amount = amount;
        }

        public boolean isSuccess() { return success; }
        public String getOrderId() { return orderId; }
        public String getTransactionNo() { return transactionNo; }
        public String getResponseCode() { return responseCode; }
        public String getMessage() { return message; }
        public long getAmount() { return amount; }
    }

    public String createVNPayPaymentUrl(Order order, HttpServletRequest request) throws UnsupportedEncodingException {
        // Chuyển đổi USD sang VND (1 USD ~ 25,000 VND), VNPay yêu cầu nhân 100
        long amountVnd = (long) Math.round(order.getTotalAmount() * USD_TO_VND_RATE);
        long vnpAmount = amountVnd * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.VNP_VERSION);
        vnp_Params.put("vnp_Command", VNPayConfig.VNP_COMMAND);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(vnpAmount));
        vnp_Params.put("vnp_CurrCode", VNPayConfig.VNP_CURR_CODE);
        vnp_Params.put("vnp_TxnRef", order.getOrderId());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getOrderId());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");

        // Xây dựng return URL động (hỗ trợ reverse proxy như Render / Cloudflare)
        String proto = request.getHeader("X-Forwarded-Proto");
        String scheme = (proto != null && !proto.isEmpty()) ? proto : request.getScheme();
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isEmpty()) {
            host = request.getServerName();
            int serverPort = request.getServerPort();
            if (serverPort != 80 && serverPort != 443 && proto == null) {
                host += ":" + serverPort;
            }
        }
        String contextPath = request.getContextPath();
        String returnUrl = scheme + "://" + host + contextPath + "/vnpay-return";
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        TimeZone vnTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar cld = Calendar.getInstance(vnTimeZone);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(vnTimeZone);

        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                    query.append('&');
                }
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayUtil.hmacSHA512(VNPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return VNPayConfig.VNP_PAY_URL + "?" + queryUrl;
    }

    public PaymentResult verifyVNPayCallback(Map<String, String[]> requestParams) {
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                fields.put(key, values[0]);
            }
        }

        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String orderId = fields.get("vnp_TxnRef");
        String transactionNo = fields.get("vnp_TransactionNo");
        String responseCode = fields.get("vnp_ResponseCode");
        String amountStr = fields.get("vnp_Amount");
        long amount = 0;
        try {
            if (amountStr != null) {
                amount = Long.parseLong(amountStr) / 100;
            }
        } catch (NumberFormatException ignored) {}

        String calculatedHash = VNPayUtil.hashAllFields(fields, VNPayConfig.getHashSecret());
        boolean isSignatureValid = calculatedHash.equalsIgnoreCase(vnp_SecureHash);

        if (!isSignatureValid) {
            return new PaymentResult(false, orderId, transactionNo, responseCode, "Chữ ký không hợp lệ (Invalid Signature)", amount);
        }

        if ("00".equals(responseCode)) {
            return new PaymentResult(true, orderId, transactionNo, responseCode, "Thanh toán thành công qua VNPay", amount);
        } else {
            return new PaymentResult(false, orderId, transactionNo, responseCode, "Giao dịch không thành công hoặc người dùng đã hủy (Mã: " + responseCode + ")", amount);
        }
    }
}
