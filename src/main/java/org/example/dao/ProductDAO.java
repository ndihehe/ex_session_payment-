package org.example.dao;

import org.example.model.Product;
import java.util.List;

public interface ProductDAO {
    List<Product> getAllProducts();
    Product getProductByCode(String code);
}
