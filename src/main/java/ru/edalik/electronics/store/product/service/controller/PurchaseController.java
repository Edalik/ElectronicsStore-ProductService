package ru.edalik.electronics.store.product.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.edalik.electronics.store.product.service.mapper.PurchaseMapper;
import ru.edalik.electronics.store.product.service.model.dto.PurchaseDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ErrorDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ValidationErrorDto;
import ru.edalik.electronics.store.product.service.service.interfaces.PurchaseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/purchases")
@Tag(name = "Purchase Controller", description = "API для взаимодействия с покупками")
public class PurchaseController {

    private final PurchaseService purchaseService;

    private final PurchaseMapper purchaseMapper;

    @Operation(
        summary = "Покупка товара",
        description = "Совершает покупку товара"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Товар успешно куплен",
        content = @Content(schema = @Schema(implementation = PurchaseDto.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Невалидные входные данные",
        content = @Content(schema = @Schema(implementation = ValidationErrorDto.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Товар не найден",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @PostMapping
    public PurchaseDto makePurchase(
        @Parameter(
            description = "UUID товара",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @RequestParam("product-id") UUID productId,

        @Parameter(
            description = "Количество товара для покупки",
            required = true,
            example = "1"
        )
        @RequestParam @Positive int quantity
    ) {
        return purchaseMapper.toDto(purchaseService.makePurchase(productId, quantity));
    }

    @Operation(
        summary = "Покупка товара из корзины",
        description = "Совершает покупку товара из корзины"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Товар из корзины успешно куплен",
        content = @Content(schema = @Schema(implementation = PurchaseDto.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Невалидные входные данные",
        content = @Content(schema = @Schema(implementation = ValidationErrorDto.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Товар не найден или корзина не найдена",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @PostMapping("/basket")
    public PurchaseDto makePurchaseFromBasket(
        @Parameter(
            description = "UUID продукта",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @RequestParam("product-id") UUID productId
    ) {
        return purchaseMapper.toDto(purchaseService.makePurchaseFromBasket(productId));
    }

    @Operation(
        summary = "Получение покупки по идентификатору",
        description = "Возвращает покупку по идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Покупка найдена",
        content = @Content(schema = @Schema(implementation = PurchaseDto.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Покупка не найдена",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @GetMapping("/{id}")
    public PurchaseDto getPurchaseById(
        @Parameter(
            description = "UUID покупки",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @PathVariable UUID id
    ) {
        return purchaseMapper.toDto(purchaseService.getPurchaseById(id));
    }

    @Operation(
        summary = "Получение списка товаров",
        description = "Возвращает список товаров"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Товары найдены",
        content = @Content(schema = @Schema(implementation = PurchaseDto.class))
    )
    @GetMapping
    public List<PurchaseDto> getProducts() {
        return purchaseMapper.toDto(purchaseService.getPurchases());
    }

}