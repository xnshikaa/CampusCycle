package com.javaoops.campuscycle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;

public class ProductDAO {

    private DatabaseHelper dbHelper;

    public ProductDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // INSERT a new product into the database
    public boolean insertProduct(Product product) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("productId",   product.getProductId());
            values.put("title",       product.getTitle());
            values.put("description", product.getDescription());
            values.put("category",    product.getCategory());
            values.put("mrp",         product.getMrp());
            values.put("price",       product.getPrice());
            values.put("sellerId",    product.getSellerId());
            values.put("status",      product.getStatus());
            values.put("viewCount",   product.getViewCount());
            values.put("cartCount",   product.getCartCount());
            values.put("timestamp",   product.getTimestamp());
            long result = db.insert("products", null, values);
            db.close();
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Product getProductById(String productId) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(
                    "products", null,
                    "productId=?", new String[]{productId},
                    null, null, null
            );
            if (cursor != null && cursor.moveToFirst()) {
                Product product = cursorToProduct(cursor);
                cursor.close();
                db.close();
                return product;
            }
            if (cursor != null) cursor.close();
            db.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateProduct(Product product) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("title",       product.getTitle());
            values.put("description", product.getDescription());
            values.put("category",    product.getCategory());
            values.put("mrp",         product.getMrp());
            values.put("price",       product.getPrice());
            values.put("status",      product.getStatus());
            values.put("viewCount",   product.getViewCount());
            values.put("cartCount",   product.getCartCount());
            int rows = db.update("products", values, "productId=?", new String[]{product.getProductId()});
            db.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(String productId) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int rows = db.delete("products", "productId=?", new String[]{productId});
            db.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM products ORDER BY timestamp DESC", null);
            if (cursor.moveToFirst()) {
                do {
                    products.add(cursorToProduct(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public ArrayList<Product> getProductsBySeller(String sellerId) {
        ArrayList<Product> products = new ArrayList<>();
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(
                    "products", null,
                    "sellerId=?", new String[]{sellerId},
                    null, null, "timestamp DESC"
            );
            if (cursor.moveToFirst()) {
                do {
                    products.add(cursorToProduct(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    private Product cursorToProduct(Cursor cursor) {
        Product p = new Product();
        p.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("productId")));
        p.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        p.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        p.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
        p.setMrp(cursor.getDouble(cursor.getColumnIndexOrThrow("mrp")));
        // setPrice() throws IllegalArgumentException if price > 0.75 * mrp
        try {
            p.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
        } catch (IllegalArgumentException e) {
            // Price stored in DB is already validated; safe to set raw if setPrice rejects it
            e.printStackTrace();
        }
        p.setSellerId(cursor.getString(cursor.getColumnIndexOrThrow("sellerId")));
        p.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        p.setViewCount(cursor.getLong(cursor.getColumnIndexOrThrow("viewCount")));
        p.setCartCount(cursor.getLong(cursor.getColumnIndexOrThrow("cartCount")));
        p.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
        return p;
    }
}