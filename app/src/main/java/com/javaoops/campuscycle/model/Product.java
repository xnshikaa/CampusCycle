package com.javaoops.campuscycle.model;

public class Product {
    private String productId;
    private String title;
    private String description;
    private String category;
    private double mrp;
    private double price;
    private String sellerId;
    private String status;
    private long viewCount;
    private long cartCount;
    private long timestamp;
    private int imageResId;

    public String getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getMrp() {
        return mrp;
    }

    public double getPrice() {
        return price;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getStatus() {
        return status;
    }

    public long getViewCount() {
        return viewCount;
    }

    public long getCartCount() {
        return cartCount;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public void setCartCount(long cartCount) {
        this.cartCount = cartCount;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public Product(String productId, String title, String description, String category, double mrp, double price, String sellerId) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.mrp = mrp;
        this.price = price;
        this.sellerId = sellerId;
        this.status = "active";
        this.viewCount = 0;
        this.cartCount = 0;
        this.timestamp = System.currentTimeMillis();
        this.imageResId = 0; // Default
    }

    public Product(String productId, String title, String description, String category, double mrp, double price, String sellerId, int imageResId) {
        this(productId, title, description, category, mrp, price, sellerId);
        this.imageResId = imageResId;
    }

    public Product() {
    }
}
