package org.example.service;

import org.example.dao.ProductDAO;
import org.example.dao.ProductDAOImpl;
import org.example.model.Product;

import java.util.List;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAOImpl();
    }

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public Product getProductByCode(String code) {
        return productDAO.getProductByCode(code);
    }
}
