package org.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class YourCart implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<LineItem> items;

    public YourCart() {
        this.items = new ArrayList<>();
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void addLineItem(LineItem newItem) {
        if (newItem == null || newItem.getProduct() == null) {
            return;
        }

        for (LineItem item : items) {
            if (item.getProduct().getCode().equals(newItem.getProduct().getCode())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        items.add(newItem);
    }

    public void removeLineItem(String productCode) {
        if (productCode == null) return;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct().getCode().equals(productCode)) {
                items.remove(i);
                break;
            }
        }
    }

    public void updateQuantity(String productCode, int quantity) {
        if (productCode == null) return;
        for (LineItem item : items) {
            if (item.getProduct().getCode().equals(productCode)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public double getTotal() {
        double total = 0.0;
        for (LineItem item : items) {
            total += item.getTotal();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public void clear() {
        items.clear();
    }
}
