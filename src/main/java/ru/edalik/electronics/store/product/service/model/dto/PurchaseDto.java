package ru.edalik.electronics.store.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "DTO для представления покупки")
public record PurchaseDto(
    @Schema(
        description = "UUID покупки",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    UUID id,

    @Schema(
        description = "UUID пользователя",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    UUID userId,

    ProductDto product,


    @Schema(
        description = "Количество купленного товара",
        example = "1"
    )
    Integer quantity,

    @Schema(
        description = "Стоимость товара в момент покупки",
        example = "123.456"
    )
    BigDecimal price,

    @Schema(
        description = "Сумма покупки",
        example = "123.456"
    )
    BigDecimal totalPrice,

    @Schema(
        description = "Время покупки",
        example = "2025-03-16T16:22:00.138Z"
    )
    LocalDateTime purchaseTime
) {

}