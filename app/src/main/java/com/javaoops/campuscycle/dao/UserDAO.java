package com.javaoops.campuscycle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.javaoops.campuscycle.model.Buyer;
import com.javaoops.campuscycle.model.Seller;
import com.javaoops.campuscycle.model.User;

public class UserDAO {
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", user.getUserId());
        values.put("name", user.getName());
        values.put("universityId", user.getUniversityId());
        values.put("email", user.getEmail());
        values.put("role", user.getRole());
        values.put("isVerified", user.isVerified() ? 1 : 0);
        long result = db.insert("users", null, values);
        db.close();
        return result != -1;
    }

    public User getUserById(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query("users", null, "userId=?", new String[]{userId}, null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String universityId = cursor.getString(cursor.getColumnIndexOrThrow("universityId"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            int isVerified = cursor.getInt(cursor.getColumnIndexOrThrow("isVerified"));

            if ("seller".equals(role)) {
                user = new Seller(userId, name, universityId, email);
            } else {
                user = new Buyer(userId, name, universityId, email);
            }
            user.setRole(role);
            user.setVerified(isVerified == 1);
        }
        cursor.close();
        db.close();
        return user;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", user.getUserId());
        values.put("name", user.getName());
        values.put("universityId", user.getUniversityId());
        values.put("email", user.getEmail());
        values.put("role", user.getRole());
        values.put("isVerified", user.isVerified() ? 1 : 0);
        long result = db.update("users", values, "userId=?", new String[]{user.getUserId()});
        db.close();
        return result > 0;
    }

    public boolean deleteUser(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = db.delete("users", "userId=?", new String[]{userId});
        db.close();
        return result > 0;
    }
}
