package com.javaoops.campuscycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.javaoops.campuscycle.model.Product;
import com.javaoops.campuscycle.service.DemandTracker;
import com.javaoops.campuscycle.service.ProductService;

import java.util.ArrayList;

public class SellerDashboardActivity extends AppCompatActivity {

    private TextView     tvWelcome, tvProductCount, tvEmpty;
    private Button       btnAddProduct, btnNotifications, btnLogout;
    private ListView     lvProducts;

    private ProductService          productService;
    private ArrayList<Product>      productList;
    private ArrayAdapter<String>    listAdapter;
    private ArrayList<String>       displayList;

    private String sellerId;
    private String sellerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        sellerId   = prefs.getString("userId", "");
        sellerName = prefs.getString("name", "Seller");

        if (sellerId.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tvWelcome       = findViewById(R.id.tvWelcome);
        tvProductCount  = findViewById(R.id.tvProductCount);
        tvEmpty         = findViewById(R.id.tvEmpty);
        lvProducts      = findViewById(R.id.lvProducts);
        btnAddProduct   = findViewById(R.id.btnAddProduct);
        btnNotifications= findViewById(R.id.btnNotifications);
        btnLogout       = findViewById(R.id.btnLogout);

        tvWelcome.setText("Hello, " + sellerName + "!");

        productService = new ProductService(this, sellerId);
        productList    = new ArrayList<>();
        displayList    = new ArrayList<>();
        listAdapter    = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        lvProducts.setAdapter(listAdapter);

        btnAddProduct.setOnClickListener(v ->
                startActivity(new Intent(this, AddProductActivity.class))
        );

        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class))
        );

        btnLogout.setOnClickListener(v -> logout());

        lvProducts.setOnItemLongClickListener((parent, view, position, id) -> {
            Product selected = productList.get(position);
            boolean deleted  = productService.deleteProduct(selected.getProductId());
            if (deleted) {
                Toast.makeText(this, "\"" + selected.getTitle() + "\" removed.", Toast.LENGTH_SHORT).show();
                loadProducts();
            } else {
                Toast.makeText(this, "Could not delete product.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        productList = productService.getSellerProducts();
        displayList.clear();

        for (Product p : productList) {
            String item = p.getTitle()
                    + "  |  ₹" + p.getPrice()
                    + "  |  " + p.getStatus().toUpperCase()
                    + "  |  👁 " + p.getViewCount()
                    + "  🛒 " + p.getCartCount();
            displayList.add(item);
        }

        listAdapter.notifyDataSetChanged();

        if (productList.isEmpty()) {
            lvProducts.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            lvProducts.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }

        tvProductCount.setText("Total Listings: " + productList.size());

        if (!productList.isEmpty()) {
            DemandTracker.trackInBackground(this, productList, sellerId);
        }
    }

    private void logout() {
        getSharedPreferences("CampusCycleSession", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}