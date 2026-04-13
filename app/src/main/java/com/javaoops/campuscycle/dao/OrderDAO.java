package com.javaoops.campuscycle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.javaoops.campuscycle.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private DatabaseHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void insertOrder(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("orderId", order.getOrderId());
        values.put("productId", order.getProductId());
        values.put("buyerId", order.getBuyerId());
        values.put("sellerId", order.getSellerId());
        values.put("amountPaid", order.getAmountPaid());
        values.put("orderStatus", order.getOrderStatus());
        values.put("timestamp", order.getTimestamp());

        db.insert("orders", null, values);
        db.close();
    }

    public List<Order> getOrdersByBuyer(String buyerId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Order> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM orders WHERE buyerId=?", new String[]{buyerId});

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();

                order.setOrderId(cursor.getString(0));
                order.setProductId(cursor.getString(1));
                order.setBuyerId(cursor.getString(2));
                order.setSellerId(cursor.getString(3));
                order.setAmountPaid(cursor.getDouble(4));
                order.setOrderStatus(cursor.getString(5));
                order.setTimestamp(cursor.getLong(6));

                list.add(order);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    public void updateStatus(String orderId, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("orderStatus", newStatus);
        db.update("orders", values, "orderId=?", new String[]{orderId});
        db.close();
    }

    public void deleteOrder(String orderId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("orders", "orderId=?", new String[]{orderId});
        db.close();
    }

    public void updateOrder(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("orderStatus", order.getOrderStatus());
        db.update("orders", values, "orderId=?", new String[]{order.getOrderId()});
        db.close();
    }
}