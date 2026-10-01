package org.example.controller;

import org.example.model.Product;
import org.example.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/home", ""})
public class ProductListServlet extends HttpServlet {

    private final ProductService productService;

    public ProductListServlet() {
        this.productService = new ProductService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Product> products = productService.getAllProducts();
        request.setAttribute("products", products);

        request.getRequestDispatcher("/Shop.jsp").forward(request, response);
    }
}
