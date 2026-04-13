package com.javaoops.campuscycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javaoops.campuscycle.dao.OrderDAO;
import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.dao.cartDAO;
import com.javaoops.campuscycle.model.Cart;
import com.javaoops.campuscycle.model.Order;
import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView rvItems;
    private TextView tvTotal;
    private Button btnConfirm;

    private cartDAO cartDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private String currentBuyerId;
    private ArrayList<Product> checkoutItems = new ArrayList<>();
    private double totalValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        currentBuyerId = prefs.getString("userId", null);

        cartDAO = new cartDAO(this);
        productDAO = new ProductDAO(this);
        orderDAO = new OrderDAO(this);

        rvItems = findViewById(R.id.rvCheckoutItems);
        tvTotal = findViewById(R.id.tvCheckoutTotal);
        btnConfirm = findViewById(R.id.btnFinalConfirm);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        loadItems();

        btnConfirm.setOnClickListener(v -> handleFinalSecure());
    }

    private void loadItems() {
        List<String> pIds = cartDAO.getProductIdsByBuyer(currentBuyerId);
        totalValue = 0;
        checkoutItems.clear();

        for (String pId : pIds) {
            Product p = productDAO.getProductById(pId);
            if (p != null) {
                checkoutItems.add(p);
                totalValue += p.getPrice();
            }
        }

        tvTotal.setText("₹" + (int) totalValue);
        rvItems.setAdapter(new CheckoutAdapter(checkoutItems));
    }

    private void handleFinalSecure() {
        if (checkoutItems.isEmpty()) return;

        for (Product p : checkoutItems) {
            Order order = new Order();
            order.setOrderId(UUID.randomUUID().toString());
            order.setProductId(p.getProductId());
            order.setBuyerId(currentBuyerId);
            order.setSellerId(p.getSellerId());
            order.setAmountPaid(p.getPrice());
            order.setOrderStatus("Confirmed");
            order.setTimestamp(System.currentTimeMillis());
            
            orderDAO.insertOrder(order);
        }

        // Clear Cart
        cartDAO.clearCart(currentBuyerId);

        // Navigate to Success Screen
        Intent intent = new Intent(this, CheckoutSuccessActivity.class);
        startActivity(intent);
        finish();
    }

    private static class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.VH> {
        private List<Product> items;
        CheckoutAdapter(List<Product> items) { this.items = items; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_checkout_product, p, false));
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Product p = items.get(pos);
            h.name.setText(p.getTitle());
            h.price.setText("₹" + (int) p.getPrice());
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView name, price;
            VH(View v) {
                super(v);
                name = v.findViewById(R.id.tvCheckoutItemName);
                price = v.findViewById(R.id.tvCheckoutItemPrice);
            }
        }
    }
}
