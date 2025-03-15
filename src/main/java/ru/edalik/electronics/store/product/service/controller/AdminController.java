package ru.edalik.electronics.store.product.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.edalik.electronics.store.product.service.mapper.CategoryMapper;
import ru.edalik.electronics.store.product.service.mapper.ProductMapper;
import ru.edalik.electronics.store.product.service.model.dto.CategoryDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ErrorDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ValidationErrorDto;
import ru.edalik.electronics.store.product.service.service.interfaces.AdminService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/admin")
@Tag(name = "Admin Controller", description = "API для админ управления сервиса")
public class AdminController {

    private final AdminService adminService;

    private final ProductMapper productMapper;

    private final CategoryMapper categoryMapper;

    @Operation(
        summary = "Создание/изменение товара",
        description = "Создает/изменяет товар в системе"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Товар успешно создан/изменен",
        content = @Content(schema = @Schema(implementation = ProductDto.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Невалидные входные данные",
        content = @Content(schema = @Schema(implementation = ValidationErrorDto.class))
    )
    @PostMapping("/product")
    public ProductDto upsert(@RequestBody @Valid ProductDto dto) {
        return productMapper.toDto(adminService.upsertProduct(productMapper.toEntity(dto)));
    }

    @Operation(
        summary = "Удаление товара",
        description = "Удаляет товар из системы"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Товар успешно удален"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Товар не найден",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProductById(
        @Parameter(
            description = "UUID товара",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @PathVariable UUID id
    ) {
        adminService.deleteProductById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
        summary = "Создание категории",
        description = "Создает категорию в системе"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Категория успешно создана",
        content = @Content(schema = @Schema(implementation = CategoryDto.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Невалидные входные данные",
        content = @Content(schema = @Schema(implementation = ValidationErrorDto.class))
    )
    @PutMapping("/category")
    public CategoryDto upsert(@RequestBody @Valid CategoryDto dto) {
        return categoryMapper.toDto(adminService.addCategory(categoryMapper.toEntity(dto)));
    }

}