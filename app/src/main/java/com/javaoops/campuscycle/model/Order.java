package com.javaoops.campuscycle.model;

public class Order {

    private String orderId;
    private String buyerId;
    private String sellerId;
    private String productId;
    private double amountPaid;
    private String orderStatus;
    private long timestamp;

    public Order() {}

    public Order(String orderId, String buyerId, String sellerId,
                 String productId, double amountPaid) {
        this.orderId     = orderId;
        this.buyerId     = buyerId;
        this.sellerId    = sellerId;
        this.productId   = productId;
        this.amountPaid  = amountPaid;
        this.orderStatus = "pending";
        this.timestamp   = System.currentTimeMillis();
    }

    public String getOrderId()     { return orderId; }
    public String getBuyerId()     { return buyerId; }
    public String getSellerId()    { return sellerId; }
    public String getProductId()   { return productId; }
    public double getAmountPaid()  { return amountPaid; }
    public String getOrderStatus() { return orderStatus; }
    public long getTimestamp()     { return timestamp; }

    public void setOrderId(String orderId)         { this.orderId = orderId; }
    public void setBuyerId(String buyerId)         { this.buyerId = buyerId; }
    public void setSellerId(String sellerId)       { this.sellerId = sellerId; }
    public void setProductId(String productId)     { this.productId = productId; }
    public void setAmountPaid(double amountPaid)   { this.amountPaid = amountPaid; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public void setTimestamp(long timestamp)       { this.timestamp = timestamp; }
}