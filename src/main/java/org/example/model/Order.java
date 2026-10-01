package org.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String customerName;
    private String customerEmail;
    private List<LineItem> items;
    private double totalAmount;
    private String paymentMethod; // "vnpay" or "simulator"
    private String paymentStatus; // "PENDING", "SUCCESS", "FAILED"
    private String transactionNo;
    private Date orderDate;

    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = new Date();
        this.paymentStatus = "PENDING";
    }

    public Order(String orderId, String customerName, String customerEmail, List<LineItem> items, double totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.orderDate = new Date();
        this.paymentStatus = "PENDING";
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void setItems(List<LineItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
