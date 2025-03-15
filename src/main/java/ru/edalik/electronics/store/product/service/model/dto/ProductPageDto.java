package ru.edalik.electronics.store.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "DTO для представления страницы товаров")
public record ProductPageDto(
    @Schema(
        description = "Текущий номер страницы",
        example = "0"
    )
    int pageNumber,

    @Schema(
        description = "Размер страницы",
        example = "10"
    )
    int pageSize,

    @Schema(
        description = "Количество элементов на текущей странице",
        example = "5"
    )
    int elementsCount,

    @Schema(
        description = "Общее количество элементов",
        example = "100"
    )
    long totalElements,

    @Schema(
        description = "Общее количество страниц",
        example = "10"
    )
    int totalPages,

    @Schema(
        description = "Список товаров на странице"
    )
    List<ProductDto> elements
) {

}