package com.javaoops.campuscycle.util;

import com.javaoops.campuscycle.model.Product;

import java.util.List;

public interface Searchable {
    List<Product> search(String query);
    List<Product> filterByCategory(String category);
}