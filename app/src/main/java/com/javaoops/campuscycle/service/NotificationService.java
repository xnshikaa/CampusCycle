package com.javaoops.campuscycle.service;

import android.content.Context;

import com.javaoops.campuscycle.dao.NotificationDAO;
import com.javaoops.campuscycle.model.Notification;
import com.javaoops.campuscycle.util.Notifiable;

import java.util.ArrayList;
import java.util.UUID;

public class NotificationService implements Notifiable {

    private final NotificationDAO notificationDAO;
    private final String          targetUserId;

    public NotificationService(Context context, String targetUserId) {
        this.notificationDAO = new NotificationDAO(context);
        this.targetUserId    = targetUserId;
    }

    @Override
    public boolean sendNotification(String message) {
        return sendNotification(message, "", "high_demand");
    }

    public boolean sendNotification(String message, String productId, String type) {
        try {
            String notifId = UUID.randomUUID().toString();
            Notification n = new Notification(
                    notifId,
                    targetUserId,
                    productId,
                    type,         // "sold" or "high_demand"
                    message,
                    false,
                    System.currentTimeMillis()
            );
            return notificationDAO.insertNotification(n);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean markAsRead(String notificationID) {
        try {
            return notificationDAO.markAsRead(notificationID);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAllAsRead() {
        try {
            return notificationDAO.markAllAsRead(targetUserId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Notification> getMyNotifications() {
        return notificationDAO.getNotificationsByUser(targetUserId);
    }
}