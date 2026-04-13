package com.javaoops.campuscycle.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "campuscycle.db";
    private static final int DATABASE_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (userId TEXT PRIMARY KEY, name TEXT, universityId TEXT, email TEXT, role TEXT, isVerified INTEGER)");
        db.execSQL("CREATE TABLE products (productId TEXT PRIMARY KEY, title TEXT, description TEXT, category TEXT," +
                "mrp REAL, price REAL, sellerId TEXT, status TEXT, viewCount INTEGER, cartCount INTEGER, timestamp INTEGER, imageResId INTEGER, imageUri TEXT)");
        db.execSQL("CREATE TABLE orders (orderId TEXT PRIMARY KEY, productId TEXT, buyerId TEXT, sellerId TEXT, amountPaid REAL, orderStatus TEXT, timestamp INTEGER)");
        db.execSQL("CREATE TABLE notifications(notifId TEXT PRIMARY KEY, targetUserId TEXT, productId TEXT, type TEXT, message TEXT, isRead INTEGER, timestamp INTEGER)");
        db.execSQL("CREATE TABLE cart (buyerId TEXT, productId TEXT, timestamp INTEGER)");
        db.execSQL("CREATE TABLE offers (offerId TEXT PRIMARY KEY, productId TEXT, buyerId TEXT, sellerId TEXT, " +
                "offerAmount REAL, status TEXT, timestamp INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS notifications");
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }
}
