package com.javaoops.campuscycle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.javaoops.campuscycle.model.Notification;

import java.util.ArrayList;

public class NotificationDAO {

    private DatabaseHelper dbHelper;

    public NotificationDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean insertNotification(Notification n) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("notifId",      n.getNotificationId());
            values.put("targetUserId", n.getTargetUserId());
            values.put("productId",    n.getProductId());
            values.put("type",         n.getType());
            values.put("message",      n.getMessage());
            values.put("isRead",       n.isRead() ? 1 : 0);
            values.put("timestamp",    n.getTimestamp());
            long result = db.insert("notifications", null, values);
            db.close();
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public ArrayList<Notification> getNotificationsByUser(String userId) {
        ArrayList<Notification> notifications = new ArrayList<>();
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(
                    "notifications", null,
                    "targetUserId=?", new String[]{userId},
                    null, null, "timestamp DESC"
            );
            if (cursor.moveToFirst()) {
                do {
                    Notification n = new Notification();
                    n.setNotificationId(cursor.getString(cursor.getColumnIndexOrThrow("notifId")));
                    n.setTargetUserId(cursor.getString(cursor.getColumnIndexOrThrow("targetUserId")));
                    n.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("productId")));
                    n.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                    n.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
                    n.setRead(cursor.getInt(cursor.getColumnIndexOrThrow("isRead")) == 1);
                    n.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
                    notifications.add(n);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public boolean markAsRead(String notifId) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("isRead", 1);
            int rows = db.update("notifications", values, "notifId=?", new String[]{notifId});
            db.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAllAsRead(String userId) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("isRead", 1);
            int rows = db.update("notifications", values, "targetUserId=?", new String[]{userId});
            db.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}