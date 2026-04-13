package com.javaoops.campuscycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AccountActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileID;
    private Button btnLogout;
    private FloatingActionButton fabAddProduct;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileID   = findViewById(R.id.tvProfileID);
        btnLogout     = findViewById(R.id.btnLogout);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        String name = prefs.getString("name", "User");
        String userId = prefs.getString("userId", "Unknown");

        tvProfileName.setText(name);
        tvProfileID.setText("ID: " + userId.substring(0, Math.min(userId.length(), 8)));

        // Setup Bottom Navigation
        bottomNavigation.setSelectedItemId(R.id.nav_account);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home || id == R.id.nav_market) {
                startActivity(new Intent(this, MarketplaceActivity.class));
                return true;
            } else if (id == R.id.nav_sell) {
                startActivity(new Intent(this, SellerDashboardActivity.class));
                return true;
            } else if (id == R.id.nav_account) {
                return true;
            }
            return false;
        });

        // Setup FAB
        fabAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(this, AddProductActivity.class));
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        getSharedPreferences("CampusCycleSession", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_account);
        }
    }
}
