package org.example.service;

import org.example.model.LineItem;
import org.example.model.Order;
import org.example.util.MailUtil;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class EmailService {

    public boolean sendOrderSuccessEmail(Order order) {
        if (order == null || order.getCustomerEmail() == null || order.getCustomerEmail().trim().isEmpty()) {
            System.err.println("[EmailService] Không thể gửi email: Địa chỉ email người nhận trống.");
            return false;
        }

        String recipientEmail = order.getCustomerEmail().trim();
        String subject = "Xác nhận thanh toán đơn hàng thành công #" + order.getOrderId();
        String htmlBody = buildOrderSuccessEmailHtml(order);

        try {
            MailUtil.sendMail(
                    recipientEmail,
                    MailUtil.getSenderEmail(),
                    subject,
                    htmlBody,
                    true
            );
            System.out.println("[EmailService] Đã gửi email xác nhận đơn hàng tới: " + recipientEmail);
            return true;
        } catch (Exception e) {
            System.err.println("[EmailService] Gửi email thất bại: " + e.getMessage());
            return false;
        }
    }

    private String buildOrderSuccessEmailHtml(Order order) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String formattedDate = dateFormat.format(order.getOrderDate());

        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        long amountVnd = (long) Math.round(order.getTotalAmount() * PaymentService.USD_TO_VND_RATE);

        StringBuilder itemsRows = new StringBuilder();
        for (LineItem item : order.getItems()) {
            itemsRows.append("<tr>")
                    .append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(item.getProduct().getDescription()).append("</td>")
                    .append("<td style='padding: 10px; border: 1px solid #ddd; text-align: center;'>").append(item.getQuantity()).append("</td>")
                    .append("<td style='padding: 10px; border: 1px solid #ddd; text-align: right;'>$").append(String.format("%.2f", item.getProduct().getPrice())).append("</td>")
                    .append("<td style='padding: 10px; border: 1px solid #ddd; text-align: right;'>$").append(String.format("%.2f", item.getTotal())).append("</td>")
                    .append("</tr>");
        }

        return "<div style='font-family: Arial, sans-serif; max-width: 650px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;'>"
                + "<div style='background-color: #006666; color: white; padding: 15px 20px; border-radius: 6px 6px 0 0; text-align: center;'>"
                + "  <h2 style='margin: 0;'>XÁC NHẬN THANH TOÁN THÀNH CÔNG</h2>"
                + "</div>"
                + "<div style='padding: 20px; background-color: #fafafa;'>"
                + "  <p>Xin chào <b>" + (order.getCustomerName() != null ? order.getCustomerName() : "Quý khách") + "</b>,</p>"
                + "  <p>Cảm ơn bạn đã mua hàng tại <b>CD Store</b>. Đơn hàng của bạn đã được thanh toán thành công với chi tiết như sau:</p>"
                + "  <table style='width: 100%; margin-bottom: 20px; font-size: 14px;'>"
                + "    <tr><td><b>Mã đơn hàng:</b></td><td>#" + order.getOrderId() + "</td></tr>"
                + "    <tr><td><b>Mã giao dịch:</b></td><td>" + (order.getTransactionNo() != null ? order.getTransactionNo() : "N/A") + "</td></tr>"
                + "    <tr><td><b>Thời gian:</b></td><td>" + formattedDate + "</td></tr>"
                + "    <tr><td><b>Phương thức:</b></td><td>" + ("vnpay".equalsIgnoreCase(order.getPaymentMethod()) ? "Cổng thanh toán VNPay" : "Thanh toán mô phỏng Test") + "</td></tr>"
                + "    <tr><td><b>Trạng thái:</b></td><td><span style='color: green; font-weight: bold;'>ĐÃ THANH TOÁN</span></td></tr>"
                + "  </table>"
                + "  <h3 style='color: #006666;'>Chi tiết sản phẩm</h3>"
                + "  <table style='width: 100%; border-collapse: collapse; margin-bottom: 20px; background-color: white;'>"
                + "    <thead>"
                + "      <tr style='background-color: #f2f2f2;'>"
                + "        <th style='padding: 10px; border: 1px solid #ddd; text-align: left;'>Sản phẩm</th>"
                + "        <th style='padding: 10px; border: 1px solid #ddd; text-align: center;'>Số lượng</th>"
                + "        <th style='padding: 10px; border: 1px solid #ddd; text-align: right;'>Đơn giá</th>"
                + "        <th style='padding: 10px; border: 1px solid #ddd; text-align: right;'>Thành tiền</th>"
                + "      </tr>"
                + "    </thead>"
                + "    <tbody>"
                + itemsRows.toString()
                + "    </tbody>"
                + "  </table>"
                + "  <div style='text-align: right; margin-top: 10px;'>"
                + "    <p style='margin: 5px 0; font-size: 16px;'>Tổng cộng (USD): <b>$" + String.format("%.2f", order.getTotalAmount()) + "</b></p>"
                + "    <p style='margin: 5px 0; font-size: 18px; color: #006666;'>Tổng thanh toán (VND): <b>" + vndFormat.format(amountVnd) + "</b></p>"
                + "  </div>"
                + "  <hr style='border: none; border-top: 1px solid #ddd; margin: 25px 0;'/>"
                + "  <p style='font-size: 13px; color: #666; text-align: center;'>Nếu có bất kỳ thắc mắc nào, vui lòng phản hồi lại email này để được hỗ trợ.<br/>Chúc bạn một ngày tuyệt vời!</p>"
                + "</div>"
                + "</div>";
    }
}
