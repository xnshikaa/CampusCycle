package com.javaoops.campuscycle.model;

public class Notification {
    private String notificationId;
    private String targetUserId;
    private String productId;
    private String type;
    private String message;
    private boolean isRead;
    private long timestamp;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Notification(String notificationId, String targetUserId, String productId, String type,
                        String message, boolean isRead, long timestamp){
        this.notificationId = notificationId;
        this.targetUserId = targetUserId;
        this.productId = productId;
        this.type = type;
        this.message = message;
        this.isRead = isRead;
        this.timestamp = timestamp;
    }

    public Notification(){

    }
}
