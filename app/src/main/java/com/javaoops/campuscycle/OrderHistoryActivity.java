package com.javaoops.campuscycle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javaoops.campuscycle.dao.OrderDAO;
import com.javaoops.campuscycle.dao.cartDAO;
import com.javaoops.campuscycle.model.Cart;
import com.javaoops.campuscycle.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private TextView tvProfileName, tvOrderCount, tvEmpty;
    private Button btnAll, btnPending, btnConfirmed, btnCompleted;
    private RecyclerView rvOrders;

    private OrderDAO orderDAO;
    private cartDAO cartDAO;
    private List<Order> allOrders = new ArrayList<>();
    private List<Order> filteredOrders = new ArrayList<>();
    private OrderAdapter orderAdapter;

    private String currentBuyerId;
    private String currentStatusFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        currentBuyerId = prefs.getString("userId", null);
        String buyerName = prefs.getString("name", "The Curator");

        if (currentBuyerId == null) {
            Toast.makeText(this, "Session expired.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderDAO = new OrderDAO(this);
        cartDAO = new cartDAO(this);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvOrderCount  = findViewById(R.id.tvOrderCount);
        tvEmpty       = findViewById(R.id.tvEmpty);
        rvOrders      = findViewById(R.id.rvOrders);
        btnAll        = findViewById(R.id.btnFilterAll);
        btnPending    = findViewById(R.id.btnFilterPending);
        btnConfirmed  = findViewById(R.id.btnFilterConfirmed);
        btnCompleted  = findViewById(R.id.btnFilterCompleted);

        tvProfileName.setText(buyerName);

        // Setup RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(filteredOrders);
        rvOrders.setAdapter(orderAdapter);

        btnAll.setOnClickListener(v       -> applyFilter("all"));
        btnPending.setOnClickListener(v   -> applyFilter("pending"));
        btnConfirmed.setOnClickListener(v -> applyFilter("confirmed"));
        btnCompleted.setOnClickListener(v -> applyFilter("completed"));

        loadOrders();
    }

    private void loadOrders() {
        try {
            allOrders.clear();
            
            // 1. Load actual orders (Confirmed/Completed)
            List<Order> realOrders = orderDAO.getOrdersByBuyer(currentBuyerId);
            allOrders.addAll(realOrders);
            
            // 2. Load Cart as "Pending" (Virtual orders)
            List<Cart> carts = cartDAO.getCartByBuyer(currentBuyerId);
            for (Cart c : carts) {
                for (String pId : c.getProductIds()) {
                    Order virtual = new Order();
                    virtual.setOrderId("pending_" + pId);
                    virtual.setProductId(pId);
                    virtual.setBuyerId(currentBuyerId);
                    virtual.setOrderStatus("Pending");
                    virtual.setTimestamp(c.getTimestamp());
                    // Placeholder amount, will be fetched by adapter
                    virtual.setAmountPaid(0); 
                    allOrders.add(virtual);
                }
            }
            
            applyFilter(currentStatusFilter);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load history", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyFilter(String status) {
        currentStatusFilter = status;

        filteredOrders.clear();
        for (Order o : allOrders) {
            if (status.equals("all") || o.getOrderStatus().equalsIgnoreCase(status)) {
                filteredOrders.add(o);
            }
        }

        updateFilterButtons();

        tvOrderCount.setText("Curated " + allOrders.size() + " Items");

        if (filteredOrders.isEmpty()) {
            rvOrders.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
        }
        orderAdapter.notifyDataSetChanged();
    }

    private void updateFilterButtons() {
        Button[] btns = {btnAll, btnPending, btnConfirmed, btnCompleted};
        for (Button b : btns) {
            b.setBackgroundResource(R.drawable.segmented_control_bg);
            b.setTextColor(getResources().getColor(R.color.text_body));
        }

        Button active = btnAll;
        if (currentStatusFilter.equals("pending"))   active = btnPending;
        if (currentStatusFilter.equals("confirmed")) active = btnConfirmed;
        if (currentStatusFilter.equals("completed")) active = btnCompleted;

        active.setBackgroundResource(R.drawable.btn_primary_gradient);
        active.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}