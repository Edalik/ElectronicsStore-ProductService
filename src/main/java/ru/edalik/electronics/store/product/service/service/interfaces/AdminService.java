package ru.edalik.electronics.store.product.service.service.interfaces;

import ru.edalik.electronics.store.product.service.model.entity.Category;
import ru.edalik.electronics.store.product.service.model.entity.Product;

import java.util.UUID;

public interface AdminService {

    Product upsertProduct(Product product);

    void deleteProductById(UUID id);

    Category addCategory(Category category);

}