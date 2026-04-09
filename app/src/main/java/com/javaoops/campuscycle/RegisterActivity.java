package com.javaoops.campuscycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.javaoops.campuscycle.dao.UserDAO;
import com.javaoops.campuscycle.model.Buyer;
import com.javaoops.campuscycle.model.Seller;
import com.javaoops.campuscycle.model.User;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etUniversityId;
    private RadioGroup rgRole;
    private Button btnRegister;
    private TextView tvError, tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_register);

        etName         = findViewById(R.id.etRegName);
        etEmail        = findViewById(R.id.etRegEmail);
        etUniversityId = findViewById(R.id.etRegUniversityId);
        rgRole         = findViewById(R.id.rgRole);
        btnRegister    = findViewById(R.id.btnRegister);
        tvError        = findViewById(R.id.tvRegError);
        tvGoToLogin    = findViewById(R.id.tvGoToLogin);

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

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address.");
            return;
        }

        String role;
        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        if (selectedRoleId == R.id.rbSeller) {
            role = "seller";
        } else {
            role = "buyer";
        }

        String userId = UUID.randomUUID().toString();

        User user;
        if (role.equals("seller")) {
            user = new Seller(userId, name, universityId, email);
        } else {
            user = new Buyer(userId, name, universityId, email);
        }

        UserDAO userDAO = new UserDAO(this);
        boolean success = userDAO.insertUser(user);

        if (success) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
