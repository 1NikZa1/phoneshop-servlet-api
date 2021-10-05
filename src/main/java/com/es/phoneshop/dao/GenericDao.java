package com.es.phoneshop.dao;

public interface GenericDao<T>{
    void save(T item);
    T get(Long id);
}
