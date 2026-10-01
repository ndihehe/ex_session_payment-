package DB;

/**
 * @deprecated Use {@link org.example.model.Product} instead.
 */
@Deprecated
public class Product extends org.example.model.Product {
    public Product() {
        super();
    }

    public Product(String description, String code, double price) {
        super(description, code, price);
    }
}