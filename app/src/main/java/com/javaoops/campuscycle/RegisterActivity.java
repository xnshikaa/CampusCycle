package com.javaoops.campuscycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.javaoops.campuscycle.dao.UserDAO;
import com.javaoops.campuscycle.model.User;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etUniversityId;
    private Button btnRegister;
    private TextView tvError, tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_register);

        etName          = findViewById(R.id.etRegName);
        etEmail         = findViewById(R.id.etRegEmail);
        etUniversityId  = findViewById(R.id.etRegUniversityId);
        btnRegister     = findViewById(R.id.btnRegister);
        tvError         = findViewById(R.id.tvRegError);
        tvGoToLogin     = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> handleRegister());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void handleRegister() {
        String name         = etName.getText().toString().trim();
        String email        = etEmail.getText().toString().trim();
        String universityId = etUniversityId.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || universityId.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        String emailPattern = "^[a-zA-Z0-9._%+-]+@(atlasskilltech\\.university|[a-zA-Z0-9.-]+\\.edu\\.in)$";
        if (!email.matches(emailPattern)) {
            showError("Only university emails allowed");
            return;
        }

        String userId = UUID.randomUUID().toString();

        User user = new User(userId, name, universityId, email);

        UserDAO userDAO = new UserDAO(this);
        boolean success = userDAO.insertUser(user);

        if (success) {
            // AUTO-LOGIN: Save session immediately
            getSharedPreferences("CampusCycleSession", MODE_PRIVATE)
                    .edit()
                    .putString("userId", userId)
                    .putString("name", name)
                    .apply();

            Toast.makeText(this, "Welcome to the Loop, " + name, Toast.LENGTH_SHORT).show();
            
            // REDIRECTION: Always Marketplace
            Intent intent = new Intent(this, MarketplaceActivity.class);
            startActivity(intent);
            finish();
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}

