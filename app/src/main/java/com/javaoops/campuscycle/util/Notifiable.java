package com.javaoops.campuscycle.util;

public interface Notifiable {
    boolean sendNotification(String notification);
    boolean markAsRead(String notificationID);
}
