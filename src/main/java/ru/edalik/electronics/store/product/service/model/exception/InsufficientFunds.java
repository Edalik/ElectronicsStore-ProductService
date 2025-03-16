package ru.edalik.electronics.store.product.service.model.exception;

public class InsufficientFunds extends RuntimeException {

    public InsufficientFunds() {
        super("Insufficient funds");
    }

}