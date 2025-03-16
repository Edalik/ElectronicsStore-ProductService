package ru.edalik.electronics.store.product.service.service;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.edalik.electronics.store.product.service.client.UserServiceClient;
import ru.edalik.electronics.store.product.service.model.dto.BalanceDto;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.entity.Purchase;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientFunds;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientQuantityException;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.repository.ProductRepository;
import ru.edalik.electronics.store.product.service.repository.PurchaseRepository;
import ru.edalik.electronics.store.product.service.service.interfaces.PurchaseService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final ProductRepository productRepository;

    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public Purchase makePurchase(UUID userId, UUID productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product with id: %s not found".formatted(productId)));

        if (product.getQuantity() < quantity) {
            throw new InsufficientQuantityException();
        }

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        try {
            userServiceClient.payment(new BalanceDto(totalPrice), userId);
        } catch (FeignException feignException) {
            if (feignException.status() == 400) throw new InsufficientFunds();
            else throw feignException;
        }

        Purchase purchase = Purchase.builder()
            .userId(userId)
            .product(product)
            .quantity(quantity)
            .price(product.getPrice())
            .totalPrice(totalPrice)
            .build();

        productRepository.decreaseQuantity(quantity, productId);

        return purchaseRepository.save(purchase);
    }

    @Override
    public Purchase getPurchaseById(UUID userId, UUID purchaseId) {
        return purchaseRepository.findById(purchaseId, userId)
            .orElseThrow(() -> new NotFoundException("Purchase with id: %s not found".formatted(purchaseId)));
    }

    @Override
    public List<Purchase> getPurchases(UUID userId) {
        return purchaseRepository.findByUserId(userId);
    }

}
