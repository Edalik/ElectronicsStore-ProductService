package ru.edalik.electronics.store.product.service.service.interfaces;

import ru.edalik.electronics.store.product.service.model.entity.Purchase;

import java.util.List;
import java.util.UUID;

public interface PurchaseService {

    Purchase makePurchase(UUID userId, UUID productId, int quantity);

    Purchase getPurchaseById(UUID userId, UUID purchaseId);

    List<Purchase> getPurchases(UUID userId);

}