package com.javaoops.campuscycle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javaoops.campuscycle.dao.OfferDAO;
import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.dao.cartDAO;
import com.javaoops.campuscycle.model.Cart;
import com.javaoops.campuscycle.model.Offer;
import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView ivProduct;
    private TextView tvTitle, tvPrice, tvMrp, tvDiscount, tvDesc, tvSellerName;
    private Button btnSecureLoop, btnChat;
    private RecyclerView rvRecommended;

    private String productId;
    private Product product;
    private ProductDAO productDAO;
    private cartDAO cartDAO;
    private OfferDAO offerDAO;
    private String currentBuyerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("productId");
        productDAO = new ProductDAO(this);
        cartDAO = new cartDAO(this);
        offerDAO = new OfferDAO(this);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        currentBuyerId = prefs.getString("userId", null);

        initViews();
        loadProduct();
        setupRecommended();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ivProduct     = findViewById(R.id.ivDetailProduct);
        tvTitle       = findViewById(R.id.tvDetailTitle);
        tvPrice       = findViewById(R.id.tvDetailPrice);
        tvMrp         = findViewById(R.id.tvDetailMrp);
        tvDiscount    = findViewById(R.id.tvDetailDiscount);
        tvDesc        = findViewById(R.id.tvDetailDesc);
        tvSellerName  = findViewById(R.id.tvSellerName);
        btnSecureLoop = findViewById(R.id.btnSecureLoop);
        btnChat       = findViewById(R.id.btnChat);
        rvRecommended = findViewById(R.id.rvRecommended);

        btnSecureLoop.setOnClickListener(v -> addToCart());
    }

    private void loadProduct() {
        product = productDAO.getProductById(productId);
        if (product != null) {
            tvTitle.setText(product.getTitle());
            tvPrice.setText("₹" + (int)product.getPrice());
            tvMrp.setText("₹" + (int)product.getMrp());
            tvDesc.setText(product.getDescription());
            tvSellerName.setText(product.getSellerId().equals("system") ? "Campus Official" : product.getSellerId());

            if (product.getImageResId() != 0) {
                ivProduct.setImageResource(product.getImageResId());
            }

            if (product.getSellerId().equals("system")) {
                btnChat.setText("Coming Soon");
                btnChat.setEnabled(false);
                btnChat.setAlpha(0.6f);
            }

            if (product.getMrp() > product.getPrice()) {
                int discount = (int) (((product.getMrp() - product.getPrice()) / product.getMrp()) * 100);
                tvDiscount.setText(discount + "% OFF");
                tvDiscount.setVisibility(View.VISIBLE);
            } else {
                tvDiscount.setVisibility(View.GONE);
            }
        }
    }

    private void addToCart() {
        if (product == null || currentBuyerId == null) return;

        cartDAO.addToCart(currentBuyerId, product.getProductId());
        
        Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
    }

    private void setupRecommended() {
        rvRecommended.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ArrayList<Product> all = productDAO.getAllProducts();
        ArrayList<Product> recs = new ArrayList<>();
        
        // Show up to 5 other products
        for (Product p : all) {
            if (!p.getProductId().equals(productId)) {
                recs.add(p);
                if (recs.size() >= 5) break;
            }
        }
        
        ProductAdapter adapter = new ProductAdapter(recs);
        adapter.setOnItemClickListener(p -> {
            // Re-load with new product
            productId = p.getProductId();
            loadProduct();
            setupRecommended(); // refresh list
        });
        adapter.setOnAddToCartListener(p -> {
             cartDAO.addToCart(currentBuyerId, p.getProductId());
              Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
        });
        rvRecommended.setAdapter(adapter);
    }
}
