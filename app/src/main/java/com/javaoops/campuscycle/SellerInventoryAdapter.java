package com.javaoops.campuscycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;

public class SellerInventoryAdapter extends RecyclerView.Adapter<SellerInventoryAdapter.InventoryViewHolder> {

    private ArrayList<Product> inventoryList;
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onActionClick(Product product);
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.listener = listener;
    }

    public SellerInventoryAdapter(ArrayList<Product> inventoryList) {
        this.inventoryList = inventoryList;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Product product = inventoryList.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvStats.setText("₹" + String.format("%.0f", product.getPrice()) + " • " + product.getViewCount() + " Views");

        holder.btnAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActionClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStats;
        Button btnAction;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStats = itemView.findViewById(R.id.tvStats);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
