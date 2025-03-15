package ru.edalik.electronics.store.product.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.edalik.electronics.store.product.service.mapper.ProductMapper;
import ru.edalik.electronics.store.product.service.model.dto.ProductDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductPageDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductSpecificationDto;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.repository.ProductRepository;
import ru.edalik.electronics.store.product.service.repository.specification.ProductSpecificationBuilder;
import ru.edalik.electronics.store.product.service.service.interfaces.ProductService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductSpecificationBuilder productSpecificationBuilder;

    private final ProductMapper productMapper;

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product with id: %s not found".formatted(id)));
    }

    @Override
    public ProductPageDto getProducts(ProductSpecificationDto dto) {
        if (dto == null) {
            dto = ProductSpecificationDto.builder().build();
        }

        Specification<Product> specification = productSpecificationBuilder.getSpecification(dto);
        Pageable pageable = productSpecificationBuilder.getPageable(dto);
        Page<Product> page = productRepository.findAll(specification, pageable);
        List<ProductDto> products = productMapper.toDto(page.getContent());

        return ProductPageDto.builder()
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .elementsCount(products.size())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .elements(products)
            .build();
    }

}