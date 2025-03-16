package ru.edalik.electronics.store.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "DTO для представления товара")
public record BasketDto(
    ProductDto product,

    @Schema(
        description = "Количество товара в корзине",
        example = "1"
    )
    Integer quantity
) {

}