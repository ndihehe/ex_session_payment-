package org.example.controller;

import org.example.model.User;
import org.example.model.YourCart;
import org.example.service.CartService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final CartService cartService;

    public RegisterServlet() {
        this.cartService = new CartService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String productCode = request.getParameter("productCode");

        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            request.setAttribute("productCode", productCode);
            request.setAttribute("message", "Vui lòng nhập địa chỉ email hợp lệ!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        User user = new User(username != null ? username.trim() : "", password != null ? password.trim() : "", email.trim());
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        if (productCode != null && !productCode.trim().isEmpty()) {
            YourCart cart = cartService.getCart(session);
            cartService.addToCart(cart, productCode.trim(), 1);
            session.setAttribute("cart", cart);
            request.getRequestDispatcher("/Cate.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
