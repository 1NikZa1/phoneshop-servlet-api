package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.GenericDao;
import com.es.phoneshop.model.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

public class ArrayListGenericDao<T extends Item> implements GenericDao<T> {
    protected List<T> items = new ArrayList<>();
    protected long id;

    @Override
    public synchronized void save(T item) {
        if (item.getId() == null) {
            item.setId(++id);
            items.add(item);
        } else {
            int productIndex = IntStream
                    .range(0, items.size())
                    .filter(i -> item.getId().equals(items.get(i).getId()))
                    .findAny()
                    .orElse(-1);
            if (productIndex == -1) {
                items.add(item);
                id = Math.max(item.getId(), id);
            } else {
                items.set(productIndex, item);
            }
        }
    }

    @Override
    public synchronized T get(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        return items.stream()
                .filter(item -> id.equals(item.getId()))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }
}
