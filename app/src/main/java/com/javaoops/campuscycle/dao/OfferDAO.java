package com.javaoops.campuscycle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.javaoops.campuscycle.model.Offer;

import java.util.ArrayList;

public class OfferDAO {
    private DatabaseHelper dbHelper;

    public OfferDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean createOffer(Offer offer) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("offerId", offer.getOfferId());
        values.put("productId", offer.getProductId());
        values.put("buyerId", offer.getBuyerId());
        values.put("sellerId", offer.getSellerId());
        values.put("offerAmount", offer.getOfferAmount());
        values.put("status", offer.getStatus());
        values.put("timestamp", offer.getTimestamp());
        
        long result = db.insert("offers", null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<Offer> getOffersBySeller(String sellerId) {
        ArrayList<Offer> offers = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("offers", null, "sellerId=?", new String[]{sellerId}, null, null, "timestamp DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Offer offer = new Offer();
                offer.setOfferId(cursor.getString(cursor.getColumnIndexOrThrow("offerId")));
                offer.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("productId")));
                offer.setBuyerId(cursor.getString(cursor.getColumnIndexOrThrow("buyerId")));
                offer.setSellerId(cursor.getString(cursor.getColumnIndexOrThrow("sellerId")));
                offer.setOfferAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("offerAmount")));
                offer.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                offer.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
                offers.add(offer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return offers;
    }

    public boolean updateOfferStatus(String offerId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        int rows = db.update("offers", values, "offerId=?", new String[]{offerId});
        db.close();
        return rows > 0;
    }
}
