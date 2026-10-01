package org.example.service;

import org.example.model.LineItem;
import org.example.model.Product;
import org.example.model.YourCart;

import javax.servlet.http.HttpSession;

public class CartService {

    private static final String CART_SESSION_KEY = "cart";
    private final ProductService productService;

    public CartService() {
        this.productService = new ProductService();
    }

    public CartService(ProductService productService) {
        this.productService = productService;
    }

    public YourCart getCart(HttpSession session) {
        YourCart cart = (YourCart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new YourCart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addToCart(YourCart cart, String productCode, int quantity) {
        if (cart == null || productCode == null || quantity <= 0) {
            return;
        }

        Product product = productService.getProductByCode(productCode);
        if (product != null) {
            LineItem item = new LineItem(product, quantity);
            cart.addLineItem(item);
        }
    }

    public void updateQuantity(YourCart cart, String productCode, int quantity) {
        if (cart == null || productCode == null) {
            return;
        }

        if (quantity <= 0) {
            cart.removeLineItem(productCode);
        } else {
            cart.updateQuantity(productCode, quantity);
        }
    }

    public void removeItem(YourCart cart, String productCode) {
        if (cart != null && productCode != null) {
            cart.removeLineItem(productCode);
        }
    }

    public void clearCart(HttpSession session) {
        YourCart cart = (YourCart) session.getAttribute(CART_SESSION_KEY);
        if (cart != null) {
            cart.clear();
        }
    }
}
