package com.javaoops.campuscycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;

public class MarketplaceActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch;
    private Button btnCart, btnOrderHistory;
    private Button btnCatAll, btnCatBooks, btnCatElectronics, btnCatClothing, btnCatFurniture, btnCatOther;
    private ListView lvProducts;
    private TextView tvEmpty;

    private ProductDAO productDAO;
    private ArrayList<Product> allProducts = new ArrayList<>();
    private ArrayList<Product> filteredProducts = new ArrayList<>();

    private String currentBuyerId;
    private String currentCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        currentBuyerId = prefs.getString("userId", null);

        if (currentBuyerId == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        productDAO = new ProductDAO(this);

        etSearch           = findViewById(R.id.etSearch);
        btnSearch          = findViewById(R.id.btnSearch);
        lvProducts         = findViewById(R.id.lvProducts);
        tvEmpty            = findViewById(R.id.tvEmpty);
        btnCart            = findViewById(R.id.btnCart);
        btnOrderHistory    = findViewById(R.id.btnOrderHistory);
        btnCatAll          = findViewById(R.id.btnCatAll);
        btnCatBooks        = findViewById(R.id.btnCatBooks);
        btnCatElectronics  = findViewById(R.id.btnCatElectronics);
        btnCatClothing     = findViewById(R.id.btnCatClothing);
        btnCatFurniture    = findViewById(R.id.btnCatFurniture);
        btnCatOther        = findViewById(R.id.btnCatOther);

        btnCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        btnOrderHistory.setOnClickListener(v ->
                startActivity(new Intent(this, OrderHistoryActivity.class)));

        btnSearch.setOnClickListener(v -> applyFilters());

        btnCatAll.setOnClickListener(v          -> selectCategory("All"));
        btnCatBooks.setOnClickListener(v        -> selectCategory("Books"));
        btnCatElectronics.setOnClickListener(v  -> selectCategory("Electronics"));
        btnCatClothing.setOnClickListener(v     -> selectCategory("Clothing"));
        btnCatFurniture.setOnClickListener(v    -> selectCategory("Furniture"));
        btnCatOther.setOnClickListener(v        -> selectCategory("Other"));

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product selected = filteredProducts.get(position);
            addToCart(selected);
        });

        loadProducts();
    }

    private void loadProducts() {
        try {
            ArrayList<Product> all = productDAO.getAllProducts();
            allProducts.clear();
            for (Product p : all) {
                if ("active".equals(p.getStatus())) {
                    allProducts.add(p);
                }
            }
            applyFilters();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectCategory(String category) {
        currentCategory = category;

        int grey  = 0xFFE0E0E0;
        int green = 0xFF2E7D32;
        Button[] allBtns = {btnCatAll, btnCatBooks, btnCatElectronics, btnCatClothing, btnCatFurniture, btnCatOther};
        for (Button b : allBtns) {
            b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(grey));
            b.setTextColor(0xFF424242);
        }

        Button active = btnCatAll;
        switch (category) {
            case "Books":       active = btnCatBooks;       break;
            case "Electronics": active = btnCatElectronics; break;
            case "Clothing":    active = btnCatClothing;    break;
            case "Furniture":   active = btnCatFurniture;   break;
            case "Other":       active = btnCatOther;       break;
        }
        active.setBackgroundTintList(android.content.res.ColorStateList.valueOf(green));
        active.setTextColor(0xFFFFFFFF);

        applyFilters();
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().trim().toLowerCase();

        filteredProducts.clear();
        for (Product p : allProducts) {
            boolean matchesCategory = currentCategory.equals("All") ||
                    p.getCategory().equalsIgnoreCase(currentCategory);
            boolean matchesSearch = query.isEmpty() ||
                    p.getTitle().toLowerCase().contains(query) ||
                    p.getDescription().toLowerCase().contains(query) ||
                    p.getCategory().toLowerCase().contains(query);

            if (matchesCategory && matchesSearch) {
                filteredProducts.add(p);
            }
        }

        updateUI();
    }

    private void updateUI() {
        if (filteredProducts.isEmpty()) {
            lvProducts.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvProducts.setVisibility(View.VISIBLE);

            ArrayList<String> displayList = new ArrayList<>();
            for (Product p : filteredProducts) {
                displayList.add(
                        p.getTitle() + "\n" +
                                p.getCategory() + "  •  ₹" + String.format("%.2f", p.getPrice()) +
                                "  (MRP ₹" + String.format("%.2f", p.getMrp()) + ")\n" +
                                p.getDescription()
                );
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    displayList
            );
            lvProducts.setAdapter(adapter);
        }
    }

    private void addToCart(Product product) {
        try {
            SharedPreferences cartPrefs = getSharedPreferences("cart_" + currentBuyerId, MODE_PRIVATE);
            String existingCart = cartPrefs.getString("cart_items", "");

            if (existingCart.contains(product.getProductId())) {
                Toast.makeText(this, "Already in cart.", Toast.LENGTH_SHORT).show();
                return;
            }

            String updatedCart = existingCart.isEmpty()
                    ? product.getProductId()
                    : existingCart + "," + product.getProductId();

            cartPrefs.edit().putString("cart_items", updatedCart).apply();

            // Increment cartCount in DB
            product.setCartCount(product.getCartCount() + 1);
            productDAO.updateProduct(product);

            Toast.makeText(this, "\"" + product.getTitle() + "\" added to cart!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Could not add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}