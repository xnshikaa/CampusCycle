package com.javaoops.campuscycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.javaoops.campuscycle.dao.OfferDAO;
import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.model.Offer;
import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;
import java.util.UUID;

public class SellerDashboardActivity extends AppCompatActivity {

    private TextView tvActiveCount, tvSoldCount, tvEarnings, tvViewCount, tvEmpty, tvScore;
    private EditText etTitle, etPrice;
    private Spinner spCategory;
    private Button btnAddProduct, btnExport, btnNewBatch;
    private ProgressBar scoreGauge;
    private RecyclerView rvInventory;
    private View btnLogout;
    private FloatingActionButton fabAddProduct;
    private BottomNavigationView bottomNavigation;

    private ProductDAO productDAO;
    private OfferDAO offerDAO;
    private ArrayList<Product> myProducts = new ArrayList<>();
    private ArrayList<Offer> pendingOffers = new ArrayList<>();
    private SellerInventoryAdapter inventoryAdapter;
    private OfferAdapter offerAdapter;
    private String currentSellerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        currentSellerId = prefs.getString("userId", null);

        if (currentSellerId == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        productDAO = new ProductDAO(this);
        offerDAO = new OfferDAO(this);

        // Bind Views
        tvActiveCount = findViewById(R.id.tvActiveCount);
        tvSoldCount   = findViewById(R.id.tvSoldCount);
        tvEarnings    = findViewById(R.id.tvEarnings);
        tvViewCount   = findViewById(R.id.tvViewCount);
        tvScore       = findViewById(R.id.tvScore);
        scoreGauge    = findViewById(R.id.scoreGauge);
        tvEmpty       = findViewById(R.id.tvEmpty);
        
        etTitle       = findViewById(R.id.etTitle);
        etPrice       = findViewById(R.id.etPrice);
        spCategory    = findViewById(R.id.spCategory);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnExport     = findViewById(R.id.btnExport);
        btnNewBatch   = findViewById(R.id.btnNewBatch);
        
        rvInventory   = findViewById(R.id.lvProducts);
        btnLogout     = findViewById(R.id.btnLogout);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Setup Bottom Navigation
        bottomNavigation.setSelectedItemId(R.id.nav_sell);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MarketplaceActivity.class));
                return true;
            } else if (id == R.id.nav_market) {
                startActivity(new Intent(this, MarketplaceActivity.class));
                return true;
            } else if (id == R.id.nav_sell) {
                return true;
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            }
            return false;
        });

        // Setup FAB
        fabAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(this, AddProductActivity.class));
        });

        setupOffersRecyclerView();
        setupCategorySpinner();
        setupRecyclerView();

        btnAddProduct.setOnClickListener(v -> handleListItem());
        btnExport.setOnClickListener(v -> Toast.makeText(this, "Generating CSV Report...", Toast.LENGTH_SHORT).show());
        btnNewBatch.setOnClickListener(v -> {
            startActivity(new Intent(this, AddProductActivity.class));
        });
        
        btnLogout.setOnClickListener(v -> logout());

        loadDashboard();
    }

    private void setupCategorySpinner() {
        String[] categories = {"Books", "Tech", "Cycle", "Gear", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        rvInventory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        inventoryAdapter = new SellerInventoryAdapter(myProducts);
        inventoryAdapter.setOnActionClickListener(this::showInventoryActions);
        rvInventory.setAdapter(inventoryAdapter);
    }

    private void setupOffersRecyclerView() {
        RecyclerView rvOffers = findViewById(R.id.rvOffers);
        rvOffers.setLayoutManager(new LinearLayoutManager(this));
        offerAdapter = new OfferAdapter(pendingOffers, new OfferAdapter.OnOfferActionListener() {
            @Override
            public void onAccept(Offer offer) {
                handleOfferAction(offer, "accepted");
            }

            @Override
            public void onDecline(Offer offer) {
                handleOfferAction(offer, "declined");
            }
        }, productDAO);
        rvOffers.setAdapter(offerAdapter);
    }

    private void handleOfferAction(Offer offer, String status) {
        if (offerDAO.updateOfferStatus(offer.getOfferId(), status)) {
            if ("accepted".equalsIgnoreCase(status)) {
                Product p = productDAO.getProductById(offer.getProductId());
                if (p != null) {
                    p.setStatus("reserved");
                    productDAO.updateProduct(p);
                }
                Toast.makeText(this, "Offer Accepted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Offer Declined", Toast.LENGTH_SHORT).show();
            }
            loadDashboard();
        }
    }

    private void handleListItem() {
        String title = etTitle.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        if (title.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Define all object properties", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setTitle(title);
        product.setCategory(category);
        product.setMrp(price * 1.2); 
        product.setPrice(price);
        product.setSellerId(currentSellerId);
        product.setStatus("active");
        product.setTimestamp(System.currentTimeMillis());
        product.setViewCount(0);

        if (productDAO.insertProduct(product)) {
            Toast.makeText(this, "Object in circulation.", Toast.LENGTH_SHORT).show();
            etTitle.setText("");
            etPrice.setText("");
            loadDashboard();
        }
    }

    private void loadDashboard() {
        myProducts.clear();
        myProducts.addAll(productDAO.getProductsBySeller(currentSellerId));
        inventoryAdapter.notifyDataSetChanged();

        loadOffers();

        int activeCount = 0;
        int soldCount = 0;
        double earnings = 0;
        long totalViews = 0;

        for (Product p : myProducts) {
            if ("sold".equalsIgnoreCase(p.getStatus())) {
                soldCount++;
                earnings += p.getPrice();
            } else {
                activeCount++;
            }
            totalViews += p.getViewCount();
        }

        tvActiveCount.setText(String.valueOf(activeCount));
        tvSoldCount.setText(String.valueOf(soldCount));
        tvEarnings.setText("₹" + String.format("%.0f", earnings));
        
        // Formatting views with 'k' if large
        if (totalViews + 1200 > 1000) {
            tvViewCount.setText(String.format("%.1fk", (totalViews + 1200) / 1000.0));
        } else {
            tvViewCount.setText(String.valueOf(totalViews + 1200));
        }

        // LIVE SELLER SCORE CALCULATION
        calculateScore(activeCount, soldCount);

        if (myProducts.isEmpty()) {
            rvInventory.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvInventory.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void loadOffers() {
        pendingOffers.clear();
        pendingOffers.addAll(offerDAO.getOffersBySeller(currentSellerId));
        offerAdapter.notifyDataSetChanged();
        
        TextView tvOffersHeader = findViewById(R.id.tvOffersHeader);
        RecyclerView rvOffers = findViewById(R.id.rvOffers);
        if (pendingOffers.isEmpty()) {
            tvOffersHeader.setVisibility(View.GONE);
            rvOffers.setVisibility(View.GONE);
        } else {
            tvOffersHeader.setVisibility(View.VISIBLE);
            rvOffers.setVisibility(View.VISIBLE);
        }
    }

    private void calculateScore(int active, int sold) {
        int total = active + sold;
        if (total == 0) {
            tvScore.setText("0");
            scoreGauge.setProgress(0);
            return;
        }

        // Formula: Base 85 + (Success Rate bonus)
        double successRate = (double) sold / total;
        int score = (int) (85 + (successRate * 15));
        if (score > 100) score = 100;

        tvScore.setText(String.valueOf(score));
        scoreGauge.setProgress(score);
    }

    private void showInventoryActions(Product product) {
        String[] options = {"Mark as Sold", "Update Valuation", "Remove"};
        new AlertDialog.Builder(this)
                .setTitle(product.getTitle())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        product.setStatus("Sold");
                        productDAO.updateProduct(product);
                        loadDashboard();
                    } else if (which == 1) {
                        showEditValuation(product);
                    } else if (which == 2) {
                        productDAO.deleteProduct(product.getProductId());
                        loadDashboard();
                    }
                })
                .show();
    }

    private void showEditValuation(Product p) {
        EditText input = new EditText(this);
        input.setText(String.valueOf((int)p.getPrice()));
        new AlertDialog.Builder(this)
                .setTitle("Update Valuation")
                .setView(input)
                .setPositiveButton("Update", (d, w) -> {
                    try {
                        p.setPrice(Double.parseDouble(input.getText().toString()));
                        productDAO.updateProduct(p);
                        loadDashboard();
                    } catch (Exception e) {
                        Toast.makeText(this, "Invalid valuation", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void logout() {
        getSharedPreferences("CampusCycleSession", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_sell);
        }
        loadDashboard();
    }
}
