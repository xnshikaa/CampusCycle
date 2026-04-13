package com.javaoops.campuscycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.model.Offer;
import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private ArrayList<Offer> offerList;
    private OnOfferActionListener listener;
    private ProductDAO productDAO;

    public interface OnOfferActionListener {
        void onAccept(Offer offer);
        void onDecline(Offer offer);
    }

    public OfferAdapter(ArrayList<Offer> offerList, OnOfferActionListener listener, ProductDAO productDAO) {
        this.offerList = offerList;
        this.listener = listener;
        this.productDAO = productDAO;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);
        
        Product product = productDAO.getProductById(offer.getProductId());
        if (product != null) {
            holder.tvTitle.setText(product.getTitle());
        } else {
            holder.tvTitle.setText("Unknown Product");
        }

        holder.tvInfo.setText("Buyer: " + offer.getBuyerId() + " • " + formatTime(offer.getTimestamp()));
        holder.tvAmount.setText("₹" + (int)offer.getOfferAmount());

        if ("pending".equalsIgnoreCase(offer.getStatus())) {
            holder.llActions.setVisibility(View.VISIBLE);
            holder.tvStatus.setVisibility(View.GONE);
        } else {
            holder.llActions.setVisibility(View.GONE);
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(offer.getStatus().toUpperCase());
            if ("accepted".equalsIgnoreCase(offer.getStatus())) {
                holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.verified_green));
            } else {
                holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.error_red));
            }
        }

        holder.btnAccept.setOnClickListener(v -> listener.onAccept(offer));
        holder.btnDecline.setOnClickListener(v -> listener.onDecline(offer));
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    private String formatTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < 60000) return "Just now";
        if (diff < 3600000) return (diff / 60000) + "m ago";
        if (diff < 86400000) return (diff / 3600000) + "h ago";
        return (diff / 86400000) + "d ago";
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInfo, tvAmount, tvStatus;
        Button btnAccept, btnDecline;
        LinearLayout llActions;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvOfferTitle);
            tvInfo = itemView.findViewById(R.id.tvOfferInfo);
            tvAmount = itemView.findViewById(R.id.tvOfferAmount);
            tvStatus = itemView.findViewById(R.id.tvOfferStatus);
            btnAccept = itemView.findViewById(R.id.btnAcceptOffer);
            btnDecline = itemView.findViewById(R.id.btnDeclineOffer);
            llActions = itemView.findViewById(R.id.llOfferActions);
        }
    }
}
