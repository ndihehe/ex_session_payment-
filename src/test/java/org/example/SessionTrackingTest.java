package org.example;

import org.example.dao.ProductDAO;
import org.example.dao.ProductDAOImpl;
import org.example.model.Order;
import org.example.model.Product;
import org.example.model.YourCart;
import org.example.service.CartService;
import org.example.service.PaymentService;
import org.example.service.ProductService;
import org.example.util.VNPayConfig;
import org.example.util.VNPayUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SessionTrackingTest {

    private ProductDAO productDAO;
    private ProductService productService;
    private CartService cartService;
    private PaymentService paymentService;

    @BeforeEach
    public void setUp() {
        productDAO = new ProductDAOImpl();
        productService = new ProductService(productDAO);
        cartService = new CartService(productService);
        paymentService = new PaymentService();
    }

    @Test
    public void testProductRetrieval() {
        List<Product> products = productService.getAllProducts();
        assertNotNull(products);
        assertEquals(3, products.size());

        Product p1 = productService.getProductByCode("pd01");
        assertNotNull(p1);
        assertEquals("pd01", p1.getCode());
        assertEquals(14.95, p1.getPrice());
    }

    @Test
    public void testCartOperations() {
        YourCart cart = new YourCart();
        cartService.addToCart(cart, "pd01", 2);
        cartService.addToCart(cart, "pd02", 1);

        assertEquals(2, cart.getItems().size());
        // 14.95 * 2 + 12.95 * 1 = 29.90 + 12.95 = 42.85
        assertEquals(42.85, cart.getTotal());

        // Update quantity
        cartService.updateQuantity(cart, "pd01", 3);
        // 14.95 * 3 + 12.95 = 44.85 + 12.95 = 57.80
        assertEquals(57.80, cart.getTotal());

        // Remove item
        cartService.removeItem(cart, "pd02");
        assertEquals(1, cart.getItems().size());
        assertEquals(44.85, cart.getTotal());
    }

    @Test
    public void testVNPayHashAndVerification() {
        String secret = VNPayConfig.getHashSecret();
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Amount", "1000000");
        params.put("vnp_Command", "pay");
        params.put("vnp_TxnRef", "123456");
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TransactionNo", "TXN99999");

        String hash = VNPayUtil.hashAllFields(params, secret);
        assertNotNull(hash);
        assertFalse(hash.isEmpty());

        Map<String, String[]> callbackParams = new HashMap<>();
        callbackParams.put("vnp_Amount", new String[]{"1000000"});
        callbackParams.put("vnp_Command", new String[]{"pay"});
        callbackParams.put("vnp_TxnRef", new String[]{"123456"});
        callbackParams.put("vnp_ResponseCode", new String[]{"00"});
        callbackParams.put("vnp_TransactionNo", new String[]{"TXN99999"});
        callbackParams.put("vnp_SecureHash", new String[]{hash});

        PaymentService.PaymentResult result = paymentService.verifyVNPayCallback(callbackParams);
        assertTrue(result.isSuccess());
        assertEquals("123456", result.getOrderId());
        assertEquals("TXN99999", result.getTransactionNo());
    }
}
