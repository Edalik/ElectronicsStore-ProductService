package ru.edalik.electronics.store.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.springframework.data.domain.Sort;
import ru.edalik.electronics.store.product.service.model.enums.SortByOptions;

import java.math.BigDecimal;

@Builder
@Schema(description = "DTO для фильтрации и сортировки товаров")
public record ProductSpecificationDto(
    @Schema(
        description = "Номер страницы (начинается с 0)",
        example = "0",
        defaultValue = "0"
    )
    @PositiveOrZero Integer page,

    @Schema(
        description = "Количество элементов на странице",
        example = "10",
        defaultValue = "10"
    )
    @Min(10) @Max(100) Integer size,

    @Schema(
        description = "Название товара (поиск по частичному совпадению)",
        example = "Название"
    )
    String name,

    @Schema(
        description = "Категория товара"
    )
    CategoryDto category,

    @Schema(
        description = "Минимальная цена",
        example = "123.456"
    )
    @PositiveOrZero BigDecimal minPrice,

    @Schema(
        description = "Максимальная цена",
        example = "123.456"
    )
    @PositiveOrZero BigDecimal maxPrice,

    @Schema(
        description = "Поле для сортировки",
        defaultValue = "NAME"
    )
    SortByOptions sortBy,

    @Schema(
        description = "Направление сортировки",
        defaultValue = "ASC"
    )
    Sort.Direction sortDirection
) {

    public ProductSpecificationDto {
        page = (page != null) ? page : 0;
        size = (size != null) ? size : 10;
        sortBy = (sortBy != null) ? sortBy : SortByOptions.NAME;
        sortDirection = (sortDirection != null) ? sortDirection : Sort.Direction.ASC;
    }

}