package com.javaoops.campuscycle.service;

import android.content.Context;

import com.javaoops.campuscycle.dao.ProductDAO;
import com.javaoops.campuscycle.model.Product;
import com.javaoops.campuscycle.util.InvalidPriceException;
import com.javaoops.campuscycle.util.Searchable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductService implements Searchable {

    private final ProductDAO productDAO;
    private final String currentSellerId;

    public ProductService(Context context, String sellerId) {
        this.productDAO      = new ProductDAO(context);
        this.currentSellerId = sellerId;
    }

    public boolean addProduct(String title, double price) {
        return addProduct(title, "", "General", price, price);
    }

    public boolean addProduct(String title, double price, String category) {
        return addProduct(title, "", category, price, price);
    }

    public boolean addProduct(String title, String description, String category,
                              double mrp, double price) {
        try {
            if (!validatePrice(price, mrp)) {
                throw new InvalidPriceException(
                        "Price must be ≤ 75% of MRP. MRP: " + mrp + ", Price entered: " + price);
            }
            String productId = UUID.randomUUID().toString();
            Product product  = new Product(productId, title, description, category, mrp, price, currentSellerId);
            return productDAO.insertProduct(product);

        } catch (InvalidPriceException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validatePrice(double price, double mrp) {
        return price > 0 && price <= mrp * 0.75;
    }

    public boolean deleteProduct(String productId) {
        try {
            return productDAO.deleteProduct(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Product> getSellerProducts() {
        return productDAO.getProductsBySeller(currentSellerId);
    }

    @Override
    public List<Product> search(String query) {
        ArrayList<Product> all     = productDAO.getAllProducts();
        List<Product>      results = new ArrayList<>();
        String             lower   = query.toLowerCase().trim();

        for (Product p : all) {
            if (p.getTitle().toLowerCase().contains(lower)
                    || p.getDescription().toLowerCase().contains(lower)) {
                results.add(p);
            }
        }
        return results;
    }

    @Override
    public List<Product> filterByCategory(String category) {
        ArrayList<Product> all     = productDAO.getAllProducts();
        List<Product>      results = new ArrayList<>();

        for (Product p : all) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                results.add(p);
            }
        }
        return results;
    }
}