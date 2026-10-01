package DB;

import org.example.dao.ProductDAO;
import org.example.dao.ProductDAOImpl;
import org.example.model.Product;

import java.util.List;

/**
 * @deprecated Use {@link org.example.dao.ProductDAO} or {@link org.example.service.ProductService} instead.
 */
@Deprecated
public class ProductData {
    private static final ProductDAO dao = new ProductDAOImpl();

    public static List<Product> getProducts() {
        return dao.getAllProducts();
    }

    public static Product getProductByCode(String code) {
        return dao.getProductByCode(code);
    }
}