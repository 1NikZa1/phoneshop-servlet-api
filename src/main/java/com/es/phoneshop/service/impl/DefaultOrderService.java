package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ArrayListOrderDao;
import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.OrderService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {
    private OrderDao orderDao;
    private ProductDao productDao;

    private static final class InstanceHolder {
        static final DefaultOrderService instance = new DefaultOrderService();
    }

    public static DefaultOrderService getInstance() {
        return DefaultOrderService.InstanceHolder.instance;
    }

    private DefaultOrderService() {
        orderDao = ArrayListOrderDao.getInstance();
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    public synchronized Order createOrder(Cart cart) {
        Order order = new Order();

        order.setItems(cart.getItems().stream().map(cartItem -> new CartItem(cartItem.getProduct(), cartItem.getQuantity())).collect(Collectors.toList()));
        order.setSubtotal(cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));
        return order;
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    @Override
    public void placeOrder(Order order) {
        order.setSecureId(UUID.randomUUID().toString());
        orderDao.save(order);
        updateProductsStock(order);
    }

    private BigDecimal calculateDeliveryCost() {
        return new BigDecimal(5);
    }

    private void updateProductsStock(Order order) {
        order.getItems().forEach(item -> {
            Product product = productDao.get(item.getProduct().getId());
            product.setStock(product.getStock() - item.getQuantity());
            productDao.save(product);
        });
    }
}
