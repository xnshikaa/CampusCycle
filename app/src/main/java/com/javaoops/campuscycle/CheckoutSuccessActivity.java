package com.javaoops.campuscycle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class CheckoutSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_success);

        TextView tvOrderNumber = findViewById(R.id.tvOrderNumber);
        Button btnTrack        = findViewById(R.id.btnTrackJourney);
        TextView btnBack       = findViewById(R.id.btnBackToMarket);

        // Generate a mock order number if not passed
        int randomId = 10000 + new Random().nextInt(90000);
        tvOrderNumber.setText("#CC-" + randomId);

        btnTrack.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            intent.putExtra("filter", "confirmed");
            startActivity(intent);
            finish();
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MarketplaceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
