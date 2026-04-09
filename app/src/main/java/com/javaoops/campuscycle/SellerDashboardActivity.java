package com.javaoops.campuscycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.javaoops.campuscycle.model.Product;
import com.javaoops.campuscycle.service.DemandTracker;
import com.javaoops.campuscycle.service.ProductService;

import java.util.ArrayList;

public class SellerDashboardActivity extends AppCompatActivity {

    private TextView     tvWelcome, tvProductCount, tvEmpty;
    private Button       btnAddProduct, btnNotifications, btnLogout;
    private Switch       switchRole;
    private String       currentMode;
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

        switchRole = findViewById(R.id.switchRole);
        SharedPreferences modePrefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        currentMode = modePrefs.getString("currentMode", "seller");
        switchRole.setChecked(currentMode.equals("seller"));
        switchRole.setText(currentMode.equals("seller") ? "Seller Mode" : "Buyer Mode");

        switchRole.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentMode = isChecked ? "seller" : "buyer";
            switchRole.setText(isChecked ? "Seller Mode" : "Buyer Mode");
            modePrefs.edit().putString("currentMode", currentMode).apply();
            Toast.makeText(this, "Switched to " + currentMode + " mode.", Toast.LENGTH_SHORT).show();
            if (!isChecked) {
                startActivity(new Intent(this, MarketplaceActivity.class));
                finish();
            }
        });

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product selected = productList.get(position);
            showEditDeleteDialog(selected);
        });

        lvProducts.setOnItemLongClickListener((parent, view, position, id) -> {
            Product selected = productList.get(position);
            deleteProduct(selected);
            return true;
        });
    }

    private void showEditDeleteDialog(Product product) {
        String[] options = {"Edit", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(product.getTitle())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        editProduct(product);
                    } else {
                        deleteProduct(product);
                    }
                })
                .show();
    }

    private void editProduct(Product product) {
        Intent intent = new Intent(this, AddProductActivity.class);
        intent.putExtra("productId", product.getProductId());
        intent.putExtra("title", product.getTitle());
        intent.putExtra("description", product.getDescription());
        intent.putExtra("category", product.getCategory());
        intent.putExtra("mrp", product.getMrp());
        intent.putExtra("price", product.getPrice());
        startActivity(intent);
    }

    private void deleteProduct(Product product) {
        boolean deleted = productService.deleteProduct(product.getProductId());
        if (deleted) {
            Toast.makeText(this, "Product removed.", Toast.LENGTH_SHORT).show();
            loadProducts();
        } else {
            Toast.makeText(this, "Could not delete product.", Toast.LENGTH_SHORT).show();
        }
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
                    + "  |  Views: " + p.getViewCount()
                    + "  Cart: " + p.getCartCount();
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