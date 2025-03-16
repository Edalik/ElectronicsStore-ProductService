package ru.edalik.electronics.store.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "DTO для представления товара")
public record ProductDto(
    @Schema(
        description = "UUID пользователя",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    UUID id,

    @Schema(
        description = "Название товара",
        example = "Товар"
    )
    @NotBlank @Length(min = 1, max = 50) String name,

    @Schema(
        description = "Категория"
    )
    CategoryDto category,

    @Schema(
        description = "Описание товара",
        example = "Описание"
    )
    String description,

    @Schema(
        description = "Стоимость товара",
        example = "123.456"
    )
    @Positive BigDecimal price,

    @Schema(
        description = "Количество товара в наличии",
        example = "1"
    )
    @Positive Integer quantity
) {

}