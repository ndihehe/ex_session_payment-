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

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final CartService cartService;

    public CartServlet() {
        this.cartService = new CartService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        YourCart cart = cartService.getCart(session);
        request.setAttribute("cart", cart);

        request.getRequestDispatcher("/Cate.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String productCode = request.getParameter("productCode");
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        if (user == null) {
            // Chưa có người dùng trong session -> chuyển sang trang đăng ký
            request.setAttribute("productCode", productCode);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        YourCart cart = cartService.getCart(session);
        if (productCode != null && !productCode.trim().isEmpty()) {
            cartService.addToCart(cart, productCode.trim(), 1);
        }

        session.setAttribute("cart", cart);
        request.getRequestDispatcher("/Cate.jsp").forward(request, response);
    }
}
