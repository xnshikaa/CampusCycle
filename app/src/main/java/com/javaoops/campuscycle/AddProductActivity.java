package com.javaoops.campuscycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.model.Product;

import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private EditText etName, etDescription, etMRP, etPrice, etTags;
    private Spinner spinnerCategory;
    private Button btnListItem;
    private ImageButton btnBack;
    private View cvImagePicker, placeholderContainer;
    private ImageView ivProductImage;

    private int selectedImageResId = 0; // Simulation
    private final String sellerId = "S1"; // As requested

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etName          = findViewById(R.id.etProductName);
        etDescription   = findViewById(R.id.etDescription);
        etMRP           = findViewById(R.id.etMRP);
        etPrice         = findViewById(R.id.etPrice);
        etTags          = findViewById(R.id.etTags);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnListItem     = findViewById(R.id.btnListItem);
        btnBack         = findViewById(R.id.btnBack);
        cvImagePicker   = findViewById(R.id.cvImagePicker);
        ivProductImage  = findViewById(R.id.ivProductImage);
        placeholderContainer = findViewById(R.id.placeholderContainer);

        setupCategorySpinner();

        btnBack.setOnClickListener(v -> finish());
        
        btnListItem.setOnClickListener(v -> handleListItem());

        cvImagePicker.setOnClickListener(v -> simulateImagePick());
    }

    private void setupCategorySpinner() {
        String[] categories = {"Books", "Tech", "Cycle", "Gear", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void simulateImagePick() {
        // In a real app, this opens gallery. Here, we pick a categorised placeholder.
        String category = spinnerCategory.getSelectedItem().toString();
        switch (category) {
            case "Books": selectedImageResId = R.drawable.prod_books; break;
            case "Tech":  selectedImageResId = R.drawable.prod_headphones; break;
            case "Cycle": selectedImageResId = R.drawable.prod_bike; break;
            default:      selectedImageResId = R.drawable.campuscycle_logo; break;
        }
        
        ivProductImage.setImageResource(selectedImageResId);
        ivProductImage.setVisibility(View.VISIBLE);
        placeholderContainer.setVisibility(View.GONE);
        
        Toast.makeText(this, "Image analysis complete: Perfect for " + category, Toast.LENGTH_SHORT).show();
    }

    private void handleListItem() {
        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String mrpStr = etMRP.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || mrpStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double mrp = Double.parseDouble(mrpStr);
        double price = Double.parseDouble(priceStr);
        String productId = "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Product product = new Product(productId, name, desc, category, mrp, price, sellerId);
        if (selectedImageResId != 0) {
            product.setImageResId(selectedImageResId);
        }

        ProductDAO productDAO = new ProductDAO(this);
        boolean success = productDAO.insertProduct(product);

        if (success) {
            Toast.makeText(this, "Item added to inventory", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Listing failed. Please check connection.", Toast.LENGTH_SHORT).show();
        }
    }
}