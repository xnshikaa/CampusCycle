package com.javaoops.campuscycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javaoops.campuscycle.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        com.javaoops.campuscycle.dao.ProductDAO productDAO = new com.javaoops.campuscycle.dao.ProductDAO(holder.itemView.getContext());
        com.javaoops.campuscycle.model.Product p = productDAO.getProductById(order.getProductId());
        
        if (p != null) {
            holder.tvTitle.setText(p.getTitle());
            
            // Bind image
            if (p.getImageUri() != null && !p.getImageUri().isEmpty()) {
                try {
                    String uriStr = p.getImageUri();
                    if (uriStr.startsWith("/") || uriStr.startsWith("file://")) {
                        holder.ivProduct.setImageURI(android.net.Uri.fromFile(new java.io.File(uriStr.replace("file://", ""))));
                    } else {
                        holder.ivProduct.setImageURI(android.net.Uri.parse(uriStr));
                    }
                } catch (Exception e) {
                    holder.ivProduct.setImageResource(R.drawable.card_background);
                }
            } else if (p.getImageResId() != 0) {
                holder.ivProduct.setImageResource(p.getImageResId());
            } else {
                holder.ivProduct.setImageResource(R.drawable.card_background);
            }
        } else {
            holder.tvTitle.setText("Loop Item #" + order.getProductId().substring(0, 4));
            holder.ivProduct.setImageResource(R.drawable.card_background);
        }
        
        holder.tvPrice.setText("₹" + (int)order.getAmountPaid());
        holder.tvStatus.setText(order.getOrderStatus().toUpperCase());
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.tvDate.setText("Loop secured on " + sdf.format(new Date(order.getTimestamp())));

        if ("completed".equalsIgnoreCase(order.getOrderStatus())) {
            holder.tvStatus.setTextColor(0xFF4CAF50); // Verified Green
        } else if ("confirmed".equalsIgnoreCase(order.getOrderStatus())) {
            holder.tvStatus.setTextColor(0xFFA78BFA); // Electric Violet
        } else {
            holder.tvStatus.setTextColor(0xFFFFA000); // Amber for Pending
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvStatus, tvPrice;
        android.widget.ImageView ivProduct;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvOrderTitle);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvPrice = itemView.findViewById(R.id.tvOrderPrice);
            ivProduct = itemView.findViewById(R.id.ivOrderProduct);
        }
    }
}
