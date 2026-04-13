package com.javaoops.campuscycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javaoops.campuscycle.dao.OrderDAO;
import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.model.Cart;
import com.javaoops.campuscycle.model.Order;
import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnCart, btnOrderHistory, btnNotifications;
    private Button btnCatAll, btnCatBooks, btnCatElectronics, btnCatClothing, btnCatFurniture, btnCatOther;
    private RecyclerView rvProducts;
    private TextView tvEmpty;

    private ProductDAO productDAO;
    private ArrayList<Product> allProducts = new ArrayList<>();
    private ArrayList<Product> filteredProducts = new ArrayList<>();
    private ProductAdapter productAdapter;

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
        rvProducts         = findViewById(R.id.rvProducts);
        tvEmpty            = findViewById(R.id.tvEmpty);
        btnCart            = findViewById(R.id.btnCart);
        btnOrderHistory    = findViewById(R.id.btnOrderHistory);
        btnNotifications   = findViewById(R.id.btnNotifications);
        btnCatAll          = findViewById(R.id.btnCatAll);
        btnCatBooks        = findViewById(R.id.btnCatBooks);
        btnCatElectronics  = findViewById(R.id.btnCatElectronics);
        btnCatClothing     = findViewById(R.id.btnCatClothing);
        btnCatFurniture    = findViewById(R.id.btnCatFurniture);
        btnCatOther        = findViewById(R.id.btnCatOther);
        tvEmpty            = findViewById(R.id.tvEmpty);

        // Setup RecyclerView
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(filteredProducts);
        
        productAdapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(MarketplaceActivity.this, ProductDetailsActivity.class);
            intent.putExtra("productId", product.getProductId());
            startActivity(intent);
        });

        productAdapter.setOnAddToCartListener(this::addToCart);

        rvProducts.setAdapter(productAdapter);

        btnCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        btnOrderHistory.setOnClickListener(v ->
                startActivity(new Intent(this, OrderHistoryActivity.class)));

        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        // Live Search implementation
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        btnCatAll.setOnClickListener(v          -> selectCategory("All"));
        btnCatBooks.setOnClickListener(v        -> selectCategory("Books"));
        btnCatElectronics.setOnClickListener(v  -> selectCategory("Electronics"));
        btnCatClothing.setOnClickListener(v     -> selectCategory("Clothing"));
        btnCatFurniture.setOnClickListener(v    -> selectCategory("Furniture"));
        btnCatOther.setOnClickListener(v        -> selectCategory("Other"));

        seedSampleData(); // Re-enabled
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
            Toast.makeText(this, "Failed to load products", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectCategory(String category) {
        currentCategory = category;

        Button[] allBtns = {btnCatAll, btnCatBooks, btnCatElectronics, btnCatClothing, btnCatFurniture, btnCatOther};
        for (Button b : allBtns) {
            b.setBackgroundResource(R.drawable.segmented_control_bg);
            b.setTextColor(getResources().getColor(R.color.text_body));
        }

        Button active = btnCatAll;
        switch (category) {
            case "Books":       active = btnCatBooks;       break;
            case "Electronics": active = btnCatElectronics; break;
            case "Clothing":    active = btnCatClothing;    break;
            case "Furniture":   active = btnCatFurniture;   break;
            case "Other":       active = btnCatOther;       break;
        }
        active.setBackgroundResource(R.drawable.btn_primary_gradient);
        active.setTextColor(getResources().getColor(R.color.white));

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

        if (filteredProducts.isEmpty()) {
            rvProducts.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }
        productAdapter.notifyDataSetChanged();
    }

    private void seedSampleData() {
        if (productDAO.getAllProducts().isEmpty()) {
            Product[] samples = {
                new Product(java.util.UUID.randomUUID().toString(), "iPad Pro", "Liquid Retina display, M2 chip", "Electronics", 1200, 900, "system", R.drawable.prod_ipad),
                new Product(java.util.UUID.randomUUID().toString(), "Books Bundle", "Engineering & Math textbooks", "Books", 100, 75, "system", R.drawable.prod_books),
                new Product(java.util.UUID.randomUUID().toString(), "Canon Camera", "Canon EOS R6 Mark II", "Electronics", 2000, 1500, "system", R.drawable.prod_camera),
                new Product(java.util.UUID.randomUUID().toString(), "Sony Headphones", "Sony WH-1000XM5", "Electronics", 400, 300, "system", R.drawable.prod_headphones),
                new Product(java.util.UUID.randomUUID().toString(), "Mountain Bike", "21-speed Matte Black Loop Bike", "Other", 160, 120, "system", R.drawable.prod_bike),
                new Product(java.util.UUID.randomUUID().toString(), "Seiko Watch", "Automatic divers watch", "Other", 80, 60, "system", R.drawable.prod_watch)
            };
            for (Product p : samples) {
                productDAO.insertProduct(p);
            }
        }
    }

    private void addToCart(Product product) {
        if (currentBuyerId == null) {
            SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
            currentBuyerId = prefs.getString("userId", null);
        }

        if (currentBuyerId == null) {
            Toast.makeText(this, "Please log in to add items", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            com.javaoops.campuscycle.dao.cartDAO cartDAO = new com.javaoops.campuscycle.dao.cartDAO(this);
            cartDAO.addToCart(currentBuyerId, product.getProductId());

            Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Loop full or technical error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}