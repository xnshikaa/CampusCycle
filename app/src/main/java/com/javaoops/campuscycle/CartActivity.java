package com.javaoops.campuscycle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Button btn = findViewById(R.id.btnCheckout);

        btn.setOnClickListener(v -> {
            Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show();
        });
    }
}