package com.javaoops.campuscycle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.javaoops.campuscycle.dao.OrderDAO;
import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.model.Order;
import com.javaoops.campuscycle.model.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private String currentBuyerId;

    private ListView lvOrders;
    private TextView tvEmpty, tvOrderCount;
    private Button btnFilterAll, btnFilterPending, btnFilterConfirmed, btnFilterCompleted;

    private List<Order> allOrders = new ArrayList<>();
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        currentBuyerId = prefs.getString("userId", null);

        if (currentBuyerId == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderDAO = new OrderDAO(this);
        productDAO = new ProductDAO(this);

        lvOrders = findViewById(R.id.lvOrders);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvOrderCount = findViewById(R.id.tvOrderCount);

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterPending = findViewById(R.id.btnFilterPending);
        btnFilterConfirmed = findViewById(R.id.btnFilterConfirmed);
        btnFilterCompleted = findViewById(R.id.btnFilterCompleted);

        btnFilterAll.setOnClickListener(v -> setFilter("All"));
        btnFilterPending.setOnClickListener(v -> setFilter("pending"));
        btnFilterConfirmed.setOnClickListener(v -> setFilter("confirmed"));
        btnFilterCompleted.setOnClickListener(v -> setFilter("completed"));

        loadOrders();
    }

    private void setFilter(String filter) {
        currentFilter = filter;
        updateUI();
    }

    private void loadOrders() {
        allOrders = orderDAO.getOrdersByBuyer(currentBuyerId);
        updateUI();
    }

    private void updateUI() {
        List<Order> filtered = new ArrayList<>();
        for (Order o : allOrders) {
            if (currentFilter.equals("All") || currentFilter.equalsIgnoreCase(o.getOrderStatus())) {
                filtered.add(o);
            }
        }

        if (filtered.isEmpty()) {
            lvOrders.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            lvOrders.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }

        tvOrderCount.setText(filtered.size() + " orders");

        ArrayList<String> displayList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        for (Order o : filtered) {
            Product p = productDAO.getProductById(o.getProductId());
            String title = (p != null) ? p.getTitle() : "Unknown Product";
            String date = sdf.format(new Date(o.getTimestamp()));
            displayList.add(
                    title + "\n" +
                            "Status: " + o.getOrderStatus().toUpperCase() + "  |  ₹"
                            + String.format("%.2f", o.getAmountPaid()) + "\n" +
                            "Date: " + date);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList);
        lvOrders.setAdapter(adapter);
    }
}