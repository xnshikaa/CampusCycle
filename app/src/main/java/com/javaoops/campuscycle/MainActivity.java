package com.javaoops.campuscycle;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.javaoops.campuscycle.model.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ YOUR TEST CODE STARTS HERE

        Buyer buyer = new Buyer("B1", "Anshika", "ATLAS001", "anshika@atlas.edu");
        buyer.login();

        Product product = new Product("P1", "Calculator", "Casio fx-991", "Electronics", 1000, 700, "S1");

        Log.d("TEST", product.getTitle());
        Log.d("TEST", "Price: " + product.getPrice());

        Order order = new Order("O1", buyer.getUserId(), product.getProductId());

        Log.d("TEST", "Order status: " + order.getStatus());
    }
}