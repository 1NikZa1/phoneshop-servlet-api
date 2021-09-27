package com.es.phoneshop.service;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface RecentlyViewedService {
    void add(HttpServletRequest request, Product product);
    List<Product> get(HttpServletRequest request);
}
