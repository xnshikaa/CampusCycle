package com.javaoops.campuscycle.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.javaoops.campuscycle.R;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Button btn = findViewById(R.id.btnOrder);

        btn.setOnClickListener(v -> {
            Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show();
        });
    }
}