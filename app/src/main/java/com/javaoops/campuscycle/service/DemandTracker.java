package com.javaoops.campuscycle.service;

import android.content.Context;

import com.javaoops.campuscycle.model.Product;

import java.util.ArrayList;

public class DemandTracker implements Runnable {

    private static final int VIEW_THRESHOLD = 10;
    private static final int CART_THRESHOLD = 3;

    private static final Object lock = new Object();

    private final Context context;
    private final ArrayList<Product> products;
    private final String sellerId;

    public DemandTracker(Context context, ArrayList<Product> products, String sellerId) {
        this.context  = context;
        this.products = products;
        this.sellerId = sellerId;
    }

    @Override
    public void run() {
        synchronized (lock) {
            try {
                NotificationService notificationService =
                        new NotificationService(context, sellerId);

                for (Product product : products) {
                    boolean highDemand =
                            product.getViewCount() > VIEW_THRESHOLD
                                    || product.getCartCount() > CART_THRESHOLD;

                    if (highDemand) {
                        String message = "High demand for " + product.getTitle() + "! "
                                + "Total views: " + product.getViewCount()
                                + ". Total cart adds: " + product.getCartCount()
                                + ". Please consider adjusting your price.";

                        notificationService.sendNotification(
                                message,
                                product.getProductId(),
                                "high_demand"
                        );
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void trackInBackground(Context context,
                                         ArrayList<Product> products,
                                         String sellerId) {
        Thread thread = new Thread(new DemandTracker(context, products, sellerId));
        thread.setName("DemandTrackerThread");
        thread.start();
    }
}