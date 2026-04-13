package com.javaoops.campuscycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private ArrayList<Product> productList;
    private OnRemoveListener removeListener;

    public interface OnRemoveListener {
        void onRemove(Product product);
    }

    public CartAdapter(ArrayList<Product> productList, OnRemoveListener removeListener) {
        this.productList = productList;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText("₹" + (int)product.getPrice());
        holder.tvCategory.setText(product.getCategory());

        if (product.getImageUri() != null && !product.getImageUri().isEmpty()) {
            try {
                holder.ivProduct.setImageURI(android.net.Uri.parse(product.getImageUri()));
            } catch (Exception e) {
                holder.ivProduct.setImageResource(R.drawable.card_background);
            }
        } else if (product.getImageResId() != 0) {
            holder.ivProduct.setImageResource(product.getImageResId());
        }

        holder.btnRemove.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemove(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice, tvCategory;
        ImageView ivProduct;
        ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
