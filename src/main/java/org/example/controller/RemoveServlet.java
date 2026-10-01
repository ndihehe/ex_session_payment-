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

@WebServlet("/remove")
public class RemoveServlet extends HttpServlet {

    private final CartService cartService;

    public RemoveServlet() {
        this.cartService = new CartService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productCode = request.getParameter("productCode");

        HttpSession session = request.getSession();
        YourCart cart = cartService.getCart(session);

        cartService.removeItem(cart, productCode);
        session.setAttribute("cart", cart);

        request.getRequestDispatcher("/Cate.jsp").forward(request, response);
    }
}
