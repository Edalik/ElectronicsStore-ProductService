package ru.edalik.electronics.store.product.service.service.interfaces;

import ru.edalik.electronics.store.product.service.model.dto.ProductPageDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductSpecificationDto;
import ru.edalik.electronics.store.product.service.model.entity.Product;

import java.util.UUID;

public interface ProductService {

    Product getProductById(UUID id);

    ProductPageDto getProducts(ProductSpecificationDto dto);

}