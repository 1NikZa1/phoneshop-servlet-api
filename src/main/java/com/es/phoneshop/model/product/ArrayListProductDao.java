package com.es.phoneshop.model.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArrayListProductDao implements ProductDao {
    private List<Product> products = new ArrayList<>();
    private long maxId;

    public static ArrayListProductDao getInstance() {
        return InstanceHolder.instance;
    }

    private static final class InstanceHolder {
        static final ArrayListProductDao instance = new ArrayListProductDao();
    }

    private ArrayListProductDao() {
    }

    @Override
    public synchronized Product getProduct(Long id) throws RuntimeException {
        return products.stream()
                .filter(product -> id.equals(product.getId()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public List<Product> findProducts(String query, String sortField, String sortOrder) {
        List<Product> returnProducts = findProducts(query);
        if (sortField == null || sortField.isEmpty()) {
            return findProducts(query);
        }

        String orderType;
        if (sortOrder == null || sortOrder.isEmpty()) {
            orderType = "asc";
        } else {
            orderType = sortOrder;
        }

        Comparator<Product> comparator;

        if (sortField.equals("description")) {
            comparator = Comparator.comparing(product -> product.getDescription().toLowerCase());
        } else {
            comparator = Comparator.comparing(Product::getPrice);
        }

        if (orderType.equals("desc")) {
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
        products = products.stream()
                .filter(product -> !(id.equals(product.getId())))
                .collect(Collectors.toList());
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

        Predicate<Product> hasMatch = product -> Arrays.stream(words)
                .anyMatch(word -> product.getDescription().toLowerCase().contains(word));

        ToIntFunction<Product> matchCount = product -> (int) Arrays.stream(words)
                .filter(word -> product.getDescription().toLowerCase().contains(word))
                .count();

        return findProducts().stream()
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .filter(hasMatch)
                .sorted(Comparator.comparingInt(matchCount).reversed())
                .collect(Collectors.toList());
    }
}
