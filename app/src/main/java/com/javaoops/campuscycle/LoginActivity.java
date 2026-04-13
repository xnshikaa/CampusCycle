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
import com.javaoops.campuscycle.model.Buyer;
import com.javaoops.campuscycle.model.Seller;

public class LoginActivity extends AppCompatActivity {

    private EditText  etEmail, etUniversityId;
    private TextView  btnModeBuyer, btnModeSeller;
    private Button    btnLogin;
    private TextView  tvError, tvGoToRegister;

    private String selectedLoginMode = "buyer"; // Primarily for UI context

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

        btnModeBuyer   = findViewById(R.id.btnLoginModeBuyer);
        btnModeSeller  = findViewById(R.id.btnLoginModeSeller);

        setupLoginModeToggle();

        // Session check
        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        if (prefs.contains("userId")) {
            navigateByRole(prefs.getString("role", "buyer"));
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> handleLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void setupLoginModeToggle() {
        btnModeBuyer.setOnClickListener(v -> {
            selectedLoginMode = "buyer";
            btnModeBuyer.setBackgroundResource(R.drawable.segmented_control_item_selected);
            btnModeBuyer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_violet)));
            btnModeBuyer.setTextColor(getResources().getColor(R.color.white));
            
            btnModeSeller.setBackground(null);
            btnModeSeller.setTextColor(0x80FFFFFF); // 50% white
        });

        btnModeSeller.setOnClickListener(v -> {
            selectedLoginMode = "seller";
            btnModeSeller.setBackgroundResource(R.drawable.segmented_control_item_selected);
            btnModeSeller.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_violet)));
            btnModeSeller.setTextColor(getResources().getColor(R.color.white));
            
            btnModeBuyer.setBackground(null);
            btnModeBuyer.setTextColor(0x80FFFFFF); // 50% white
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
            String role   = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            int isVerified= cursor.getInt(cursor.getColumnIndexOrThrow("isVerified"));

            cursor.close();
            db.close();

            // Check if user is trying to login with wrong role selected
            if (!role.equalsIgnoreCase(selectedLoginMode)) {
                showError("Account type mismatch. Please select the correct role.");
                return;
            }

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
        if (role.equalsIgnoreCase("seller")) {
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