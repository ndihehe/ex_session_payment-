package org.example.dao;

import org.example.model.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    private static final List<Product> products = new ArrayList<>();

    static {
        products.add(new Product(
                "86 (the band) - True Life Songs and Pictures",
                "pd01",
                14.95
        ));

        products.add(new Product(
                "Paddlefoot - The first CD",
                "pd02",
                12.95
        ));

        products.add(new Product(
                "Paddlefoot - The second CD",
                "pd03",
                14.95
        ));
    }

    @Override
    public List<Product> getAllProducts() {
        return Collections.unmodifiableList(products);
    }

    @Override
    public Product getProductByCode(String code) {
        if (code == null) return null;
        for (Product product : products) {
            if (product.getCode().equalsIgnoreCase(code.trim())) {
                return product;
            }
        }
        return null;
    }
}
