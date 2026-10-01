package org.example.controller;

import org.example.model.Order;
import org.example.service.CartService;
import org.example.service.EmailService;
import org.example.service.PaymentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/vnpay-return")
public class PaymentCallbackServlet extends HttpServlet {

    private final PaymentService paymentService;
    private final EmailService emailService;
    private final CartService cartService;

    public PaymentCallbackServlet() {
        this.paymentService = new PaymentService();
        this.emailService = new EmailService();
        this.cartService = new CartService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        PaymentService.PaymentResult result = paymentService.verifyVNPayCallback(request.getParameterMap());

        Order order = (Order) session.getAttribute("pendingOrder");
        if (order == null) {
            order = new Order();
            order.setOrderId(result.getOrderId());
            order.setPaymentMethod("vnpay");
            order.setTotalAmount(result.getAmount() / PaymentService.USD_TO_VND_RATE);
        }

        if (result.isSuccess()) {
            order.setPaymentStatus("SUCCESS");
            order.setTransactionNo(result.getTransactionNo());

            // Gửi mail thông báo thanh toán thành công
            boolean emailSent = emailService.sendOrderSuccessEmail(order);

            // Xóa sạch giỏ hàng và pendingOrder sau khi thanh toán thành công
            cartService.clearCart(session);
            session.removeAttribute("pendingOrder");

            request.setAttribute("order", order);
            request.setAttribute("emailSent", emailSent);
            request.setAttribute("successMessage", "Thanh toán đơn hàng #" + order.getOrderId() + " thành công qua VNPay!");
        } else {
            order.setPaymentStatus("FAILED");
            request.setAttribute("order", order);
            request.setAttribute("errorMessage", "Thanh toán thất bại: " + result.getMessage());
        }

        request.getRequestDispatcher("/checkout_success.jsp").forward(request, response);
    }
}
