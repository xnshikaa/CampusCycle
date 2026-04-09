package com.javaoops.campuscycle;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import com.javaoops.campuscycle.dao.OrderDAO;
import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.model.Order;
import com.javaoops.campuscycle.model.Product;

import java.util.List;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private String currentBuyerId;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private List<Order> pendingOrders = new ArrayList<>();

    private ListView lvCartItems;
    private TextView tvEmpty, tvTotal, tvItemCount;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        SharedPreferences prefs = getSharedPreferences("CampusCycleSession", MODE_PRIVATE);
        currentBuyerId = prefs.getString("userId", null);
        
        if (currentBuyerId == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderDAO = new OrderDAO(this);
        productDAO = new ProductDAO(this);

        lvCartItems  = findViewById(R.id.lvCartItems);
        tvEmpty      = findViewById(R.id.tvEmpty);
        tvTotal      = findViewById(R.id.tvTotal);
        tvItemCount  = findViewById(R.id.tvItemCount);
        btnCheckout  = findViewById(R.id.btnCheckout);

        btnCheckout.setOnClickListener(v -> {
            if (pendingOrders.isEmpty()) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                for (Order o : pendingOrders) {
                    orderDAO.updateStatus(o.getOrderId(), "confirmed");
                }
                Toast.makeText(this, "Order placed successfully.", Toast.LENGTH_SHORT).show();
                refreshCart();
            }
        });

        refreshCart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCart();
    }

    private void refreshCart() {
        pendingOrders.clear();
        List<Order> allOrders = orderDAO.getOrdersByBuyer(currentBuyerId);
        
        double total = 0;
        for (Order o : allOrders) {
            if ("pending".equals(o.getOrderStatus())) {
                pendingOrders.add(o);
                total += o.getAmountPaid();
            }
        }

        if (pendingOrders.isEmpty()) {
            lvCartItems.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            lvCartItems.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }

        ArrayList<String> displayList = new ArrayList<>();
        for (Order o : pendingOrders) {
            Product p = productDAO.getProductById(o.getProductId());
            if (p != null) {
                displayList.add(
                        p.getTitle() + "\n" +
                        p.getCategory() + "  |  ₹" + String.format("%.2f", o.getAmountPaid())
                );
            } else {
                displayList.add("Unknown Product  |  ₹" + String.format("%.2f", o.getAmountPaid()));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );
        lvCartItems.setAdapter(adapter);

        tvTotal.setText("₹" + String.format("%.2f", total));
        tvItemCount.setText(pendingOrders.size() + " items");
    }
}