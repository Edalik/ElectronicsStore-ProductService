package ru.edalik.electronics.store.product.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.edalik.electronics.store.product.service.mapper.BasketMapper;
import ru.edalik.electronics.store.product.service.model.dto.BasketDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ErrorDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ValidationErrorDto;
import ru.edalik.electronics.store.product.service.service.interfaces.BasketService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/basket")
@Tag(name = "Basket Controller", description = "API для управления корзиной")
public class BasketController {

    private final BasketService basketService;

    private final BasketMapper basketMapper;

    @Operation(
        summary = "Добавление товара в корзину",
        description = "Добавляет товар в корзину пользователя"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Товар успешно добавлен в корзину",
        content = @Content(schema = @Schema(implementation = BasketDto.class))
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
    public BasketDto addToBasket(
        @Parameter(
            description = "UUID пользователя",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @RequestHeader("User-Id") UUID userId,

        @Parameter(
            description = "UUID товара",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @RequestParam("product-id") UUID productId,

        @Parameter(
            description = "Количество товара для добавления в корзину",
            required = true,
            example = "1"
        )
        @RequestParam @Positive int quantity
    ) {
        return basketMapper.toDto(basketService.addToBasket(userId, productId, quantity));
    }

    @Operation(
        summary = "Получение корзины пользователя",
        description = "Возвращает корзину пользователя"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Корзина успешно возвращена",
        content = @Content(schema = @Schema(implementation = BasketDto.class))
    )
    @GetMapping
    public List<BasketDto> getUserBasket(
        @Parameter(
            description = "UUID пользователя",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @RequestHeader("User-Id") UUID userId
    ) {
        return basketMapper.toDto(basketService.getUserBasket(userId));
    }

    @Operation(
        summary = "Удаление товара из корзины",
        description = "Удаляет товар из корзины пользователя"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Товар успешно удален из корзины"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Товар не найден",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @DeleteMapping
    public ResponseEntity<Void> removeFromBasket(
        @Parameter(
            description = "UUID пользователя",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @RequestHeader("User-Id") UUID userId,

        @Parameter(
            description = "UUID товара",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @RequestParam("product-id") UUID productId
    ) {
        basketService.removeFromBasket(userId, productId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}