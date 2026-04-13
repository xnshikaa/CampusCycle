package com.javaoops.campuscycle.model;

import java.util.List;

public class Cart {

    private String cartId;
    private String buyerId;
    private List<String> productIds;
    private long timestamp;

    public Cart() {}

    public Cart(String cartId, String buyerId, List<String> productIds) {
        this.cartId = cartId;
        this.buyerId = buyerId;
        this.productIds = productIds;
        this.timestamp = System.currentTimeMillis();
    }

    public String getCartId() {
        return cartId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
