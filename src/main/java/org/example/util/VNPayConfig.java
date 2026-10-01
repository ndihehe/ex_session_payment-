package org.example.util;

public class VNPayConfig {
    public static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_CURR_CODE = "VND";

    // Giá trị mặc định cho VNPay Sandbox test (có thể ghi đè bằng Biến môi trường hoặc .env)
    public static String getTmnCode() {
        return EnvUtil.get("VNP_TMN_CODE", "KVNCEFID");
    }

    public static String getHashSecret() {
        return EnvUtil.get("VNP_HASH_SECRET", "FBIEBEWXIKWJJJEDPBSXCJDYGTSQWKUY");
    }
}
