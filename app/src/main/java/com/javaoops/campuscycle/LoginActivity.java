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

import com.javaoops.campuscycle.dao.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText  etEmail, etUniversityId;
    private Button    btnLogin;
    private TextView  tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail        = findViewById(R.id.etEmail);
        etUniversityId = findViewById(R.id.etUniversityId);
        btnLogin       = findViewById(R.id.btnLogin);
        tvError        = findViewById(R.id.tvError);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        if (prefs.contains("userId")) {
            navigateByRole(prefs.getString("role", "buyer"));
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String email        = etEmail.getText().toString().trim();
        String universityId = etUniversityId.getText().toString().trim();

        if (email.isEmpty() || universityId.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address.");
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
            String role   = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            int isVerified= cursor.getInt(cursor.getColumnIndexOrThrow("isVerified"));

            cursor.close();
            db.close();

            getSharedPreferences("CampusCycleSession", MODE_PRIVATE)
                    .edit()
                    .putString("userId",     userId)
                    .putString("role",       role)
                    .putString("name",       name)
                    .putBoolean("isVerified", isVerified == 1)
                    .apply();

            navigateByRole(role);
            finish();

        } catch (Exception e) {
            showError("Login failed. Please try again.");
            e.printStackTrace();
        }
    }

    private void navigateByRole(String role) {
        Intent intent;
        if (role.equals("seller")) {
            intent = new Intent(this, SellerDashboardActivity.class);
        } else {
            intent = new Intent(this, MarketplaceActivity.class);
        }
        startActivity(intent);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}