package ru.edalik.electronics.store.product.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edalik.electronics.store.product.service.model.entity.Category;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.repository.CategoryRepository;
import ru.edalik.electronics.store.product.service.repository.ProductRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    static final UUID PRODUCT_ID = UUID.randomUUID();

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    AdminServiceImpl adminService;

    final Product product = mock(Product.class);

    final Category category = mock(Category.class);

    @Test
    void upsertProduct_ShouldSaveProduct_WhenValidInput() {
        when(productRepository.save(product)).thenReturn(product);

        Product result = adminService.upsertProduct(product);

        assertEquals(product, result);
    }

    @Test
    void deleteProductById_ShouldDelete_WhenProductExists() {
        when(productRepository.customDeleteById(PRODUCT_ID)).thenReturn(1);

        adminService.deleteProductById(PRODUCT_ID);

        verify(productRepository).customDeleteById(PRODUCT_ID);
    }

    @Test
    void deleteProductById_ShouldThrowException_WhenProductNotExists() {
        when(productRepository.customDeleteById(PRODUCT_ID)).thenReturn(0);

        Exception exception = assertThrows(
            NotFoundException.class,
            () -> adminService.deleteProductById(PRODUCT_ID)
        );

        assertEquals("Product with id: " + PRODUCT_ID + " was not found", exception.getMessage());
    }

    @Test
    void addCategory_ShouldSaveCategory_WhenValidInput() {
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = adminService.addCategory(category);

        assertEquals(category, result);
    }

}