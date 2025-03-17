package ru.edalik.electronics.store.product.service.service;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edalik.electronics.store.product.service.client.UserServiceClient;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.entity.Purchase;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientFunds;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientQuantityException;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.repository.ProductRepository;
import ru.edalik.electronics.store.product.service.repository.PurchaseRepository;
import ru.edalik.electronics.store.product.service.service.kafka.KafkaProducer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    static final UUID USER_ID = UUID.randomUUID();
    static final UUID PRODUCT_ID = UUID.randomUUID();
    static final UUID PURCHASE_ID = UUID.randomUUID();
    static final int QUANTITY = 2;
    static final BigDecimal PRICE = BigDecimal.valueOf(1000);

    @Mock
    PurchaseRepository purchaseRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    KafkaProducer kafkaProducer;

    @InjectMocks
    PurchaseServiceImpl purchaseService;

    @Test
    void makePurchase_ShouldCreatePurchase_WhenValid() {
        Product product = Product.builder()
            .id(PRODUCT_ID)
            .quantity(QUANTITY + 1)
            .price(PRICE)
            .build();
        Purchase purchase = mock(Purchase.class);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(purchaseRepository.save(any())).thenReturn(purchase);

        Purchase result = purchaseService.makePurchase(USER_ID, PRODUCT_ID, QUANTITY);

        assertEquals(purchase, result);

        verify(kafkaProducer).sendMessage(any());
    }

    @Test
    void makePurchase_ShouldThrowNotFoundException_WhenProductNotExists() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> purchaseService.makePurchase(USER_ID, PRODUCT_ID, QUANTITY));
    }

    @Test
    void makePurchase_ShouldThrowInsufficientQuantity_WhenNotEnoughStock() {
        Product product = Product.builder()
            .id(PRODUCT_ID)
            .quantity(QUANTITY - 1)
            .build();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        assertThrows(
            InsufficientQuantityException.class,
            () -> purchaseService.makePurchase(USER_ID, PRODUCT_ID, QUANTITY)
        );
    }

    @Test
    void makePurchase_ShouldThrowInsufficientFunds_WhenPaymentFails() {
        Product product = Product.builder()
            .id(PRODUCT_ID)
            .quantity(QUANTITY + 1)
            .price(PRICE)
            .build();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        FeignException feignException = mock(FeignException.class);
        doThrow(feignException).when(userServiceClient).payment(any(), eq(USER_ID));
        when(feignException.status()).thenReturn(400);

        assertThrows(InsufficientFunds.class, () -> purchaseService.makePurchase(USER_ID, PRODUCT_ID, QUANTITY));
    }

    @Test
    void getPurchaseById_ShouldReturnPurchase_WhenExists() {
        Purchase expected = mock(Purchase.class);
        when(purchaseRepository.findById(PURCHASE_ID, USER_ID)).thenReturn(Optional.of(expected));

        Purchase result = purchaseService.getPurchaseById(USER_ID, PURCHASE_ID);

        assertEquals(expected, result);
    }

    @Test
    void getPurchaseById_ShouldThrowNotFoundException_WhenNotExists() {
        when(purchaseRepository.findById(PURCHASE_ID, USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> purchaseService.getPurchaseById(USER_ID, PURCHASE_ID));
    }

    @Test
    void getPurchases_ShouldReturnPurchaseList() {
        List<Purchase> expected = List.of(mock(Purchase.class));
        when(purchaseRepository.findByUserId(USER_ID)).thenReturn(expected);

        List<Purchase> result = purchaseService.getPurchases(USER_ID);

        assertEquals(expected, result);
    }

}