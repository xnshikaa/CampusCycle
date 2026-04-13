package com.javaoops.campuscycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.dao.cartDAO;
import com.javaoops.campuscycle.model.Cart;
import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private TextView tvItemCount, tvTotal, tvEmpty;
    private RecyclerView rvCart;
    private Button btnCheckout;

    private cartDAO cartDAO;
    private ProductDAO productDAO;
    private ArrayList<Product> cartProducts = new ArrayList<>();
    private CartAdapter cartAdapter;

    private String currentBuyerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        currentBuyerId = prefs.getString("userId", null);

        if (currentBuyerId == null) {
            Toast.makeText(this, "Session expired.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartDAO = new cartDAO(this);
        productDAO = new ProductDAO(this);

        tvItemCount = findViewById(R.id.tvItemCount);
        tvTotal     = findViewById(R.id.tvTotal);
        tvEmpty     = findViewById(R.id.tvEmpty);
        rvCart      = findViewById(R.id.lvCartItems);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        
        cartAdapter = new CartAdapter(cartProducts, product -> {
            cartDAO.removeFromCart(currentBuyerId, product.getProductId());
            loadCart();
            Toast.makeText(this, "Removed from Loop", Toast.LENGTH_SHORT).show();
        });
        
        rvCart.setAdapter(cartAdapter);

        btnCheckout.setOnClickListener(v -> handleCheckout());

        loadCart();
    }

    private void loadCart() {
        try {
            List<String> pIds = cartDAO.getProductIdsByBuyer(currentBuyerId);
            cartProducts.clear();
            double total = 0;
            
            for (String pId : pIds) {
                Product p = productDAO.getProductById(pId);
                if (p != null) {
                    cartProducts.add(p);
                    total += p.getPrice();
                }
            }

            tvItemCount.setText(cartProducts.size() + " ITEMS IN LOOP");
            tvTotal.setText("₹" + (int)total);

            if (cartProducts.isEmpty()) {
                rvCart.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                btnCheckout.setEnabled(false);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvCart.setVisibility(View.VISIBLE);
                btnCheckout.setEnabled(true);
            }
            cartAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load loop", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCheckout() {
        if (cartProducts.isEmpty()) {
            Toast.makeText(this, "Loop is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CheckoutActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }
}