package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;

public class ArrayListOrderDao extends ArrayListGenericDao<Order> implements OrderDao {

    private static final class InstanceHolder {
        static final ArrayListOrderDao instance = new ArrayListOrderDao();
    }

    public static ArrayListOrderDao getInstance() {
        return InstanceHolder.instance;
    }

    private ArrayListOrderDao() {
    }

    @Override
    public Order getOrderBySecureId(String id) {
        return items.stream()
                .filter(order -> id.equals(order.getSecureId()))
                .findAny()
                .orElseThrow(OrderNotFoundException::new);
    }
}
