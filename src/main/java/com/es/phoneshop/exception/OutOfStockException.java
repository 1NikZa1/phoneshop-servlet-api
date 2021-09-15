package com.es.phoneshop.exception;

public class OutOfStockException extends Exception {
    private int stockAvailable;

    public OutOfStockException(int stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }
}
