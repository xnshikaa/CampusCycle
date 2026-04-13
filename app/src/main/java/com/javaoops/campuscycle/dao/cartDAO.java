package com.javaoops.campuscycle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.javaoops.campuscycle.model.Cart;

import java.util.ArrayList;
import java.util.List;

public class cartDAO {

    private DatabaseHelper dbHelper;

    public cartDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addToCart(String buyerId, String productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("buyerId",   buyerId);
        values.put("productId", productId);
        values.put("timestamp", System.currentTimeMillis());
        db.insert("cart", null, values);
        db.close();
    }

    public List<String> getProductIdsByBuyer(String buyerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT productId FROM cart WHERE buyerId=?", new String[]{buyerId});
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void removeFromCart(String buyerId, String productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("cart", "buyerId=? AND productId=?", new String[]{buyerId, productId});
        db.close();
    }

    public void clearCart(String buyerId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("cart", "buyerId=?", new String[]{buyerId});
        db.close();
    }

    // Compatibility method for existing code
    public List<Cart> getCartByBuyer(String buyerId) {
        List<String> pIds = getProductIdsByBuyer(buyerId);
        List<Cart> list = new ArrayList<>();
        if (!pIds.isEmpty()) {
            list.add(new Cart("default", buyerId, pIds));
        }
        return list;
    }
}