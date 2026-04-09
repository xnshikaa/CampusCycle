package com.javaoops.campuscycle.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.javaoops.campuscycle.R;

public class OrderHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        TextView tv = findViewById(R.id.tvOrders);
        tv.setText("Order History Loaded");
    }
}