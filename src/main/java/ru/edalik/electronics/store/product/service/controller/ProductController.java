package ru.edalik.electronics.store.product.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.edalik.electronics.store.product.service.mapper.ProductMapper;
import ru.edalik.electronics.store.product.service.model.dto.ProductDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductPageDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductSpecificationDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ErrorDto;
import ru.edalik.electronics.store.product.service.service.interfaces.ProductService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product Controller", description = "API для взаимодействия с товарами")
public class ProductController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    @Operation(
        summary = "Получение товара по идентификатору",
        description = "Возвращает товар по идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Товар найден",
        content = @Content(schema = @Schema(implementation = ProductDto.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Товар не найден",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(
        @Parameter(
            description = "UUID товара",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @PathVariable UUID id
    ) {
        return new ResponseEntity<>(productMapper.toDto(productService.getProductById(id)), HttpStatus.OK);
    }

    @Operation(
        summary = "Получение списка товаров",
        description = "Возвращает список товаров"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Товары найдены",
        content = @Content(schema = @Schema(implementation = ProductDto.class))
    )
    @GetMapping
    public ResponseEntity<ProductPageDto> getProducts(
        @RequestBody(required = false) ProductSpecificationDto dto
    ) {
        return new ResponseEntity<>(productService.getProducts(dto), HttpStatus.OK);
    }

}