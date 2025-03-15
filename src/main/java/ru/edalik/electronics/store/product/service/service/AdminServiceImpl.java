package ru.edalik.electronics.store.product.service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.edalik.electronics.store.product.service.model.entity.Category;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.repository.CategoryRepository;
import ru.edalik.electronics.store.product.service.repository.ProductRepository;
import ru.edalik.electronics.store.product.service.service.interfaces.AdminService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    @Override
    public Product upsertProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProductById(UUID id) {
        int rowsAffected = productRepository.customDeleteById(id);
        if (rowsAffected < 1) {
            throw new NotFoundException("Product with id: %s was not found".formatted(id));
        }
    }

    @Override
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

}