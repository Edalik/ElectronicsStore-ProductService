package ru.edalik.electronics.store.product.service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.edalik.electronics.store.product.service.model.entity.Basket;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientQuantityException;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.repository.BasketRepository;
import ru.edalik.electronics.store.product.service.repository.ProductRepository;
import ru.edalik.electronics.store.product.service.service.interfaces.BasketService;
import ru.edalik.electronics.store.product.service.service.security.UserContextService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasketServiceImpl implements BasketService {

    private final BasketRepository basketRepository;

    private final ProductRepository productRepository;

    private final UserContextService userContextService;

    @Override
    public List<Basket> getUserBasket() {
        UUID userId = userContextService.getUserGuid();
        return basketRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Basket addToBasket(UUID productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product with id: %s was not found".formatted(productId)));

        if (product.getQuantity() < quantity) {
            throw new InsufficientQuantityException();
        }

        UUID userId = userContextService.getUserGuid();
        Basket.BasketId basketId = new Basket.BasketId(userId, product);
        return basketRepository.save(new Basket(basketId, quantity));
    }

    @Override
    @Transactional
    public void removeFromBasket(UUID productId) {
        UUID userId = userContextService.getUserGuid();
        int rowsAffected = basketRepository.deleteByCompositeKey(userId, productId);
        if (rowsAffected < 1) {
            throw new NotFoundException("Product with id: %s was not in basket".formatted(productId));
        }
    }

}