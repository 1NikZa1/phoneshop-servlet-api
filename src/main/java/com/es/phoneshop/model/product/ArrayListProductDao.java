package com.es.phoneshop.model.product;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArrayListProductDao implements ProductDao {
    private List<Product> products = new ArrayList<>();
    private long maxId;
    private Map<SortField, Comparator<Product>> sortComparatorMapping = Map.of(
            SortField.DESCRIPTION, Comparator.comparing(product -> product.getDescription().toLowerCase()),
            SortField.PRICE, Comparator.comparing(Product::getPrice)
    );

    public static ArrayListProductDao getInstance() {
        return InstanceHolder.instance;
    }

    private static final class InstanceHolder {
        static final ArrayListProductDao instance = new ArrayListProductDao();
    }

    private ArrayListProductDao() {
    }

    @Override
    public synchronized Product getProduct(Long id) {

        if (id == null) {
            throw new IllegalArgumentException();
        }

        return products.stream()
                .filter(product -> id.equals(product.getId()))
                .findAny()
                .orElseThrow(() -> new ProductNotFoundException(id));
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
    public synchronized void save(Product product) {
        if (product.getId() == null) {
            product.setId(++maxId);
            products.add(product);
        } else {
            int productIndex = IntStream
                    .range(0, products.size())
                    .filter(i -> product.getId().equals(products.get(i).getId()))
                    .findAny()
                    .orElse(-1);
            if (productIndex == -1) {
                products.add(product);
                maxId = Math.max(product.getId(), maxId);
            } else {
                products.set(productIndex, product);
            }
        }
    }

    @Override
    public synchronized void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        products.removeIf(product -> product.getId().equals(id));
    }

    private synchronized List<Product> findProducts() {
        return products.stream()
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
