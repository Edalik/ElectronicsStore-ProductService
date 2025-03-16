package ru.edalik.electronics.store.product.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edalik.electronics.store.product.service.model.entity.Basket;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientQuantityException;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.repository.BasketRepository;
import ru.edalik.electronics.store.product.service.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasketServiceImplTest {

    static final UUID USER_ID = UUID.randomUUID();
    static final UUID PRODUCT_ID = UUID.randomUUID();
    static final int QUANTITY = 2;

    @Mock
    BasketRepository basketRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    BasketServiceImpl basketService;

    @Test
    void getUserBasket_ShouldReturnBasketList() {
        List<Basket> expected = List.of(mock(Basket.class));
        when(basketRepository.findByUserId(USER_ID)).thenReturn(expected);

        List<Basket> result = basketService.getUserBasket(USER_ID);

        assertEquals(expected, result);
    }

    @Test
    void addToBasket_ShouldSaveNewBasketItem_WhenValid() {
        Product product = Product.builder().quantity(QUANTITY + 1).build();
        Basket expectedBasket = mock(Basket.class);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(basketRepository.save(any())).thenReturn(expectedBasket);

        Basket result = basketService.addToBasket(USER_ID, PRODUCT_ID, QUANTITY);

        assertEquals(expectedBasket, result);
    }

    @Test
    void addToBasket_ShouldThrowNotFoundException_WhenProductNotExists() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> basketService.addToBasket(USER_ID, PRODUCT_ID, QUANTITY));
    }

    @Test
    void addToBasket_ShouldThrowInsufficientQuantity_WhenNotEnoughStock() {
        Product product = Product.builder().quantity(QUANTITY - 1).build();
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        assertThrows(
            InsufficientQuantityException.class,
            () -> basketService.addToBasket(USER_ID, PRODUCT_ID, QUANTITY)
        );
    }

    @Test
    void removeFromBasket_ShouldDeleteItem_WhenExists() {
        when(basketRepository.deleteByCompositeKey(USER_ID, PRODUCT_ID)).thenReturn(1);

        assertDoesNotThrow(() -> basketService.removeFromBasket(USER_ID, PRODUCT_ID));
    }

    @Test
    void removeFromBasket_ShouldThrowException_WhenNotExists() {
        when(basketRepository.deleteByCompositeKey(USER_ID, PRODUCT_ID)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> basketService.removeFromBasket(USER_ID, PRODUCT_ID));
    }

}