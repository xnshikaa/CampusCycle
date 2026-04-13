package com.javaoops.campuscycle.model;

public class Offer {
    private String offerId;
    private String productId;
    private String buyerId;
    private String sellerId;
    private double offerAmount;
    private String status; // pending, accepted, declined
    private long timestamp;

    public Offer() {}

    public Offer(String offerId, String productId, String buyerId, String sellerId, double offerAmount) {
        this.offerId = offerId;
        this.productId = productId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.offerAmount = offerAmount;
        this.status = "pending";
        this.timestamp = System.currentTimeMillis();
    }

    public String getOfferId() { return offerId; }
    public void setOfferId(String offerId) { this.offerId = offerId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public double getOfferAmount() { return offerAmount; }
    public void setOfferAmount(double offerAmount) { this.offerAmount = offerAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
