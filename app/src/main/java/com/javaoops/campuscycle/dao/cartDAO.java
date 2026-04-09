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

    public void addToCart(Cart cart) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("cartId", cart.getCartId());
        values.put("buyerId", cart.getBuyerId());
        values.put("productId", cart.getProductId());
        values.put("timestamp", cart.getTimestamp());

        db.insert("cart", null, values);
        db.close();
    }

    public List<Cart> getCartByBuyer(String buyerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Cart> cartList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM cart WHERE buyerId=?", new String[]{buyerId});

        if (cursor.moveToFirst()) {
            do {
                Cart cart = new Cart();
                cart.setCartId(cursor.getString(0));
                cart.setBuyerId(cursor.getString(1));
                cart.setProductId(cursor.getString(2));
                cart.setTimestamp(cursor.getLong(3));

                cartList.add(cart);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return cartList;
    }

    public void removeFromCart(String cartId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("cart", "cartId=?", new String[]{cartId});
        db.close();
    }
}
