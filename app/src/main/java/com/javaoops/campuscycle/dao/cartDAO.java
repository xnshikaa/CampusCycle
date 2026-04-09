package com.javaoops.campuscycle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.javaoops.campuscycle.model.Cart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class cartDAO {

    private DatabaseHelper dbHelper;

    public cartDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addToCart(Cart cart) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (String productId : cart.getProductIds()) {
            ContentValues values = new ContentValues();
            values.put("cartId",    cart.getCartId());
            values.put("buyerId",   cart.getBuyerId());
            values.put("productId", productId);
            values.put("timestamp", cart.getTimestamp());
            db.insert("cart", null, values);
        }
        db.close();
    }

    public List<Cart> getCartByBuyer(String buyerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Map<String, Cart> cartMap = new LinkedHashMap<>();

        Cursor cursor = db.rawQuery("SELECT * FROM cart WHERE buyerId=?", new String[]{buyerId});

        if (cursor.moveToFirst()) {
            do {
                String cartId   = cursor.getString(cursor.getColumnIndexOrThrow("cartId"));
                String productId = cursor.getString(cursor.getColumnIndexOrThrow("productId"));
                long timestamp  = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));

                if (cartMap.containsKey(cartId)) {
                    cartMap.get(cartId).getProductIds().add(productId);
                } else {
                    // First time seeing this cartId — create a new Cart
                    List<String> productIds = new ArrayList<>();
                    productIds.add(productId);
                    Cart cart = new Cart(cartId, buyerId, productIds);
                    cart.setTimestamp(timestamp);
                    cartMap.put(cartId, cart);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return new ArrayList<>(cartMap.values());
    }

    public void removeFromCart(String cartId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("cart", "cartId=?", new String[]{cartId});
        db.close();
    }
}