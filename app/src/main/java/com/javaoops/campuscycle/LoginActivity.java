package com.javaoops.campuscycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.javaoops.campuscycle.dao.DatabaseHelper;
import com.javaoops.campuscycle.dao.UserDAO;

public class LoginActivity extends AppCompatActivity {

    private EditText  etEmail, etUniversityId;
    private Button    btnLogin;
    private TextView  tvError, tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_login);

        etEmail        = findViewById(R.id.etEmail);
        etUniversityId = findViewById(R.id.etUniversityId);
        btnLogin       = findViewById(R.id.btnLogin);
        tvError        = findViewById(R.id.tvError);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Session check
        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        if (prefs.contains("userId")) {
            startActivity(new Intent(this, MarketplaceActivity.class));
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> handleLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void handleLogin() {
        String email        = etEmail.getText().toString().trim();
        String universityId = etUniversityId.getText().toString().trim();

        if (email.isEmpty() || universityId.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db       = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(
                    "users", null,
                    "email=? AND universityId=?",
                    new String[]{email, universityId},
                    null, null, null
            );

            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) cursor.close();
                db.close();
                showError("No account found. Check your credentials.");
                return;
            }

            String userId = cursor.getString(cursor.getColumnIndexOrThrow("userId"));
            String name   = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int isVerified= cursor.getInt(cursor.getColumnIndexOrThrow("isVerified"));

            cursor.close();
            db.close();

            getSharedPreferences("CampusCycleSession", MODE_PRIVATE)
                    .edit()
                    .putString("userId",     userId)
                    .putString("name",       name)
                    .putBoolean("isVerified", isVerified == 1)
                    .apply();

            startActivity(new Intent(this, MarketplaceActivity.class));
            finish();

        } catch (Exception e) {
            showError("Login failed. Please try again.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}