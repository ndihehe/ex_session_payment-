package DB;

/**
 * @deprecated Use {@link org.example.model.LineItem} instead.
 */
@Deprecated
public class LineItem extends org.example.model.LineItem {
    public LineItem() {
        super();
    }

    public LineItem(org.example.model.Product product, int quantity) {
        super(product, quantity);
    }
}