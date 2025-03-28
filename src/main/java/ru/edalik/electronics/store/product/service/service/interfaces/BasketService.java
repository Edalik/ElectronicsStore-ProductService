package ru.edalik.electronics.store.product.service.service.interfaces;

import ru.edalik.electronics.store.product.service.model.entity.Basket;

import java.util.List;
import java.util.UUID;

public interface BasketService {

    List<Basket> getUserBasket();

    Basket addToBasket(UUID productId, int quantity);

    void removeFromBasket(UUID productId);

}