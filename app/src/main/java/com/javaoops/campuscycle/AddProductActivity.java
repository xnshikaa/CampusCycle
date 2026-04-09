package com.javaoops.campuscycle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.javaoops.campuscycle.service.ProductService;

public class AddProductActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etCategory, etMrp, etPrice;
    private TextView tvPriceHint, tvError;
    private Button   btnSubmit;

    private ProductService productService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        SharedPreferences prefs    = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        String            sellerId = prefs.getString("userId", "");

        productService = new ProductService(this, sellerId);

        etTitle       = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etCategory    = findViewById(R.id.etCategory);
        etMrp         = findViewById(R.id.etMrp);
        etPrice       = findViewById(R.id.etPrice);
        tvPriceHint   = findViewById(R.id.tvPriceHint);
        tvError       = findViewById(R.id.tvError);
        btnSubmit     = findViewById(R.id.btnSubmit);

        etMrp.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double mrp = Double.parseDouble(s.toString());
                    double max = mrp * 0.75;
                    tvPriceHint.setText(String.format("Max allowed: ₹%.2f", max));
                } catch (NumberFormatException e) {
                    tvPriceHint.setText("Max allowed: —");
                }
            }
        });

        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        String title       = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category    = etCategory.getText().toString().trim();
        String mrpStr      = etMrp.getText().toString().trim();
        String priceStr    = etPrice.getText().toString().trim();

        if (title.isEmpty()) {
            showError("Product title is required.");
            return;
        }
        if (mrpStr.isEmpty() || priceStr.isEmpty()) {
            showError("MRP and Selling Price are required.");
            return;
        }

        double mrp, price;
        try {
            mrp   = Double.parseDouble(mrpStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            showError("Enter valid numeric values for MRP and Price.");
            return;
        }

        if (mrp <= 0 || price <= 0) {
            showError("MRP and Price must be greater than 0.");
            return;
        }

        if (!productService.validatePrice(price, mrp)) {
            showError(String.format("Price ₹%.2f exceeds 75%% of MRP (max ₹%.2f).", price, mrp * 0.75));
            return;
        }

        if (category.isEmpty()) category = "General";

        boolean success = productService.addProduct(title, description, category, mrp, price);

        if (success) {
            Toast.makeText(this, "\"" + title + "\" listed successfully!", Toast.LENGTH_SHORT).show();
            finish(); // go back to SellerDashboardActivity
        } else {
            showError("Failed to list product. Please try again.");
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}