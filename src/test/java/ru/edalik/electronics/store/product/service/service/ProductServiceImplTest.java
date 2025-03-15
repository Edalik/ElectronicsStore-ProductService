package ru.edalik.electronics.store.product.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.edalik.electronics.store.product.service.mapper.ProductMapper;
import ru.edalik.electronics.store.product.service.model.dto.ProductDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductPageDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductSpecificationDto;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.repository.ProductRepository;
import ru.edalik.electronics.store.product.service.repository.specification.ProductSpecificationBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    static final UUID PRODUCT_ID = UUID.randomUUID();
    static final ProductSpecificationDto SPEC_DTO = ProductSpecificationDto.builder().build();

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductSpecificationBuilder specificationBuilder;

    @Mock
    ProductMapper productMapper;

    @InjectMocks
    ProductServiceImpl productService;

    Product product = mock(Product.class);

    ProductDto productDto = mock(ProductDto.class);

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(PRODUCT_ID);

        assertEquals(product, result);
    }

    @Test
    void getProductById_ShouldThrowNotFoundException_WhenProductNotExists() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductById(PRODUCT_ID));
    }

    @ParameterizedTest
    @MethodSource("getProductsArguments")
    void getProducts_ShouldReturnPaginatedResults_WithSpecification(ProductSpecificationDto specDto) {
        Specification<Product> specification = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);
        Page<Product> page = mock(Page.class);
        List<ProductDto> dtos = List.of(productDto);

        when(specificationBuilder.getSpecification(SPEC_DTO)).thenReturn(specification);
        when(specificationBuilder.getPageable(SPEC_DTO)).thenReturn(pageable);
        when(productRepository.findAll(specification, pageable)).thenReturn(page);
        when(productMapper.toDto(page.getContent())).thenReturn(dtos);

        ProductPageDto expected = ProductPageDto.builder()
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .elementsCount(dtos.size())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .elements(dtos)
            .build();

        ProductPageDto result = productService.getProducts(specDto);

        assertEquals(expected, result);
    }

    static Stream<Arguments> getProductsArguments() {
        return Stream.of(
            Arguments.of(SPEC_DTO),
            null
        );
    }

}