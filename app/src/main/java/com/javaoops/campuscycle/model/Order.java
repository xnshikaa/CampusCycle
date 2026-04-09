package com.javaoops.campuscycle.model;

public class Order {

    private String orderId;
    private String buyerId;
    private String productId;
    private String status;
    private long timestamp;

    // Empty constructor (required for flexibility)
    public Order() {}

    // Parameterized constructor
    public Order(String orderId, String buyerId, String productId) {
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.productId = productId;
        this.status = "placed"; // default status
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getProductId() {
        return productId;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
