package org.example.controller;

import org.example.model.YourCart;
import org.example.service.CartService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/update")
public class UpdateServlet extends HttpServlet {

    private final CartService cartService;

    public UpdateServlet() {
        this.cartService = new CartService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productCode = request.getParameter("productCode");
        String quantityText = request.getParameter("quantity");

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity < 1) {
                quantity = 1;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid quantity");
            return;
        }

        HttpSession session = request.getSession();
        YourCart cart = cartService.getCart(session);

        cartService.updateQuantity(cart, productCode, quantity);
        session.setAttribute("cart", cart);

        request.getRequestDispatcher("/Cate.jsp").forward(request, response);
    }
}
