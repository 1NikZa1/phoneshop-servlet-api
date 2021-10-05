package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class ArrayListProductDao extends ArrayListGenericDao<Product> implements ProductDao {
    private Map<SortField, Comparator<Product>> sortComparatorMapping = Map.of(
            SortField.DESCRIPTION, Comparator.comparing(product -> product.getDescription().toLowerCase()),
            SortField.PRICE, Comparator.comparing(Product::getPrice)
    );

    private static final class InstanceHolder {
        static final ArrayListProductDao instance = new ArrayListProductDao();
    }

    public static ArrayListProductDao getInstance() {
        return InstanceHolder.instance;
    }

    private ArrayListProductDao() {
    }

    @Override
    public synchronized List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        List<Product> returnProducts = findProducts(query);
        if (sortField == null) {
            return returnProducts;
        }

        SortOrder orderType = Objects.requireNonNullElse(sortOrder, SortOrder.ASC);

        Comparator<Product> comparator = sortComparatorMapping.get(sortField);

        if (orderType ==SortOrder.DESC) {
            comparator = comparator.reversed();
        }

        returnProducts.sort(comparator);
        return returnProducts;
    }

    @Override
    public synchronized void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        items.removeIf(product -> product.getId().equals(id));
    }

    private synchronized List<Product> findProducts() {
        return items.stream()
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());
    }

    private List<Product> findProducts(String query) {
        if (query == null || query.isEmpty()) {
            return findProducts();
        }

        String[] words = query.toLowerCase().split(" ");

        ToIntFunction<Product> matchCount = product -> (int) Arrays.stream(words)
                .filter(word -> product.getDescription().toLowerCase().contains(word))
                .count();

        return findProducts().stream()
                .filter(p -> matchCount.applyAsInt(p) > 0)
                .sorted(Comparator.comparingInt(matchCount).reversed())
                .collect(Collectors.toList());
    }
}
