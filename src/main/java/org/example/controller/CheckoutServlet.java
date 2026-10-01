package org.example.controller;

import org.example.model.Order;
import org.example.model.User;
import org.example.model.YourCart;
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

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final CartService cartService;
    private final PaymentService paymentService;
    private final EmailService emailService;

    public CheckoutServlet() {
        this.cartService = new CartService();
        this.paymentService = new PaymentService();
        this.emailService = new EmailService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        YourCart cart = cartService.getCart(session);
        if (cart == null || cart.getItems().isEmpty()) {
            request.setAttribute("message", "ur cart is empty!");
            request.getRequestDispatcher("/Cate.jsp").forward(request, response);
            return;
        }

        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        if (user == null) {
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        YourCart cart = cartService.getCart(session);
        if (cart == null || cart.getItems().isEmpty()) {
            request.setAttribute("message", "ur cart is empty!");
            request.getRequestDispatcher("/Cate.jsp").forward(request, response);
            return;
        }

        // Lấy thông tin người dùng đã có sẵn từ session
        String customerName = user.getUsername() != null && !user.getUsername().isEmpty() ? user.getUsername() : "Khách hàng";
        String customerEmail = user.getEmail();

        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "vnpay";
        }

        String orderId = String.valueOf(System.currentTimeMillis());
        Order order = new Order(orderId, customerName, customerEmail, cart.getItems(), cart.getTotal());
        order.setPaymentMethod(paymentMethod);

        if ("vnpay".equalsIgnoreCase(paymentMethod)) {
            // Lưu order đang xử lý vào session để callback kiểm tra
            session.setAttribute("pendingOrder", order);

            // Sinh URL thanh toán VNPay và chuyển hướng client
            String paymentUrl = paymentService.createVNPayPaymentUrl(order, request);
            response.sendRedirect(paymentUrl);
        } else {
            // Thanh toán thử nghiệm giả lập (Simulator)
            order.setPaymentStatus("SUCCESS");
            order.setTransactionNo("SIM-" + System.currentTimeMillis());

            // Gửi email hóa đơn xác nhận đến email của người dùng trong session
            boolean emailSent = emailService.sendOrderSuccessEmail(order);

            // Xóa sạch giỏ hàng
            cartService.clearCart(session);

            request.setAttribute("order", order);
            request.setAttribute("emailSent", emailSent);
            request.getRequestDispatcher("/checkout_success.jsp").forward(request, response);
        }
    }
}
