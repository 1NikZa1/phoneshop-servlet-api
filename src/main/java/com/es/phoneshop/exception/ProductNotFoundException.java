package com.es.phoneshop.exception;

public class ProductNotFoundException extends RuntimeException{
    private final Long productId;

    public ProductNotFoundException(Long productId) {
        this.productId = productId;
    }

    public Long getProductId(){
        return productId;
    }
}
