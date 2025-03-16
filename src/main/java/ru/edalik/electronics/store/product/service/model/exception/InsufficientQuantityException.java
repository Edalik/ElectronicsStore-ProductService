package ru.edalik.electronics.store.product.service.model.exception;

public class InsufficientQuantityException extends RuntimeException {

    public InsufficientQuantityException() {
        super("Insufficient product quantity");
    }

}