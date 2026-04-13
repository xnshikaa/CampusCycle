package com.javaoops.campuscycle;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.javaoops.campuscycle.dao.NotificationDAO;
import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.dao.UserDAO;
import com.javaoops.campuscycle.model.Notification;
import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private EditText etName, etDescription, etMRP, etPrice, etTags;
    private Spinner spinnerCategory;
    private Button btnListItem;
    private ImageButton btnBack;
    private View cvImagePicker, placeholderContainer;
    private ImageView ivProductImage;

    private String selectedImageUri = ""; 
    private final String sellerId = "S1"; 

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri.toString();
                    ivProductImage.setImageURI(uri);
                    ivProductImage.setVisibility(View.VISIBLE);
                    placeholderContainer.setVisibility(View.GONE);
                }
            }
    );

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

        cvImagePicker.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }


    private void setupCategorySpinner() {
        String[] categories = {"Books", "Tech", "Cycle", "Gear", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
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

        // STRICT Manual Validation
        if (price > 0.75 * mrp) {
            etPrice.setError("Value must be ≤ 75% of MRP (₹" + (mrp * 0.75) + ")");
            return;
        }

        String productId = "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Product product = new Product(productId, name, desc, category, mrp, price, sellerId, selectedImageUri);

        ProductDAO productDAO = new ProductDAO(this);
        try {
            boolean success = productDAO.insertProduct(product);
            if (success) {
                // NEW: Broadcast Notification to all Buyers
                broadcastNewProductNotification(product);
                
                Toast.makeText(this, "Item added to inventory", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Listing failed. Please check connection.", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Pricing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void broadcastNewProductNotification(Product product) {
        UserDAO userDAO = new UserDAO(this);
        NotificationDAO notifDAO = new NotificationDAO(this);
        ArrayList<String> buyerIds = userDAO.getAllBuyerIds();

        for (String buyerId : buyerIds) {
            Notification n = new Notification(
                    UUID.randomUUID().toString(),
                    buyerId,
                    product.getProductId(),
                    "NEW_PRODUCT",
                    "New Arrival: " + product.getTitle() + " is now in circulation!",
                    false,
                    System.currentTimeMillis()
            );
            notifDAO.insertNotification(n);
        }
    }
}