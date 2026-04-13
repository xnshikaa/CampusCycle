package com.javaoops.campuscycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;
import java.util.Random;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private ArrayList<Product> productList;
    private OnItemClickListener listener;
    private OnAddToCartListener cartListener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public interface OnAddToCartListener {
        void onAddToCart(Product product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnAddToCartListener(OnAddToCartListener listener) {
        this.cartListener = listener;
    }

    public ProductAdapter(ArrayList<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText("₹" + (int)product.getPrice());
        holder.tvBadge.setText(product.getCategory().toUpperCase());
        holder.tvSeller.setText("Looping in " + product.getCategory());

        if (product.getImageResId() != 0) {
            holder.ivProduct.setImageResource(product.getImageResId());
        } else {
            holder.ivProduct.setImageResource(R.drawable.card_background);
        }

        // Discount logic
        if (product.getMrp() > product.getPrice()) {
            int discount = (int) (((product.getMrp() - product.getPrice()) / product.getMrp()) * 100);
            holder.tvDiscount.setText(discount + "% OFF");
            holder.tvDiscount.setVisibility(View.VISIBLE);
        } else {
            holder.tvDiscount.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(product);
        });

        holder.btnAddCart.setOnClickListener(v -> {
            if (cartListener != null) cartListener.onAddToCart(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice, tvBadge, tvSeller, tvCondition, tvDiscount;
        ImageView ivProduct;
        View btnAddCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvBadge = itemView.findViewById(R.id.tvBadge);
            tvSeller = itemView.findViewById(R.id.tvSellerInfo);
            tvCondition = itemView.findViewById(R.id.tvCondition);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            btnAddCart = itemView.findViewById(R.id.btnAddCart);
        }
    }
}
