package ru.edalik.electronics.store.product.service.service;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.edalik.electronics.store.product.service.client.UserServiceClient;
import ru.edalik.electronics.store.product.service.model.dto.BalanceDto;
import ru.edalik.electronics.store.product.service.model.dto.NotificationRequest;
import ru.edalik.electronics.store.product.service.model.entity.Basket;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.entity.Purchase;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientFunds;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientQuantityException;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.repository.BasketRepository;
import ru.edalik.electronics.store.product.service.repository.ProductRepository;
import ru.edalik.electronics.store.product.service.repository.PurchaseRepository;
import ru.edalik.electronics.store.product.service.service.interfaces.PurchaseService;
import ru.edalik.electronics.store.product.service.service.kafka.KafkaProducer;
import ru.edalik.electronics.store.product.service.service.security.UserContextService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final ProductRepository productRepository;

    private final BasketRepository basketRepository;

    private final UserServiceClient userServiceClient;

    private final KafkaProducer kafkaProducer;

    private final UserContextService userContextService;

    @Override
    @Transactional
    public Purchase makePurchase(UUID productId, int quantity) {
        UUID userId = userContextService.getUserGuid();
        return makePurchase(userId, productId, quantity);
    }

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
            userServiceClient.payment(new BalanceDto(totalPrice));
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

        String notificationMessage = "Successful purchase of %s in quantity of %s was made for a total price of %s"
            .formatted(product.getName(), quantity, totalPrice);
        String email = userContextService.getEmail();
        kafkaProducer.sendMessage(
            new NotificationRequest(userId, "Purchase notification", notificationMessage, email)
        );

        return purchaseRepository.save(purchase);
    }

    @Override
    @Transactional
    public Purchase makePurchaseFromBasket(UUID productId) {
        UUID userId = userContextService.getUserGuid();
        Basket basket = basketRepository.findByCompositeKey(userId, productId)
            .orElseThrow(() -> new NotFoundException("Basket with id: %s not found".formatted(productId)));

        Purchase purchase = makePurchase(userId, productId, basket.getQuantity());

        basketRepository.deleteByCompositeKey(userId, productId);

        return purchase;
    }

    @Override
    public Purchase getPurchaseById(UUID purchaseId) {
        UUID userId = userContextService.getUserGuid();
        return purchaseRepository.findById(purchaseId, userId)
            .orElseThrow(() -> new NotFoundException("Purchase with id: %s not found".formatted(purchaseId)));
    }

    @Override
    public List<Purchase> getPurchases() {
        UUID userId = userContextService.getUserGuid();
        return purchaseRepository.findByUserId(userId);
    }

}
