package ru.edalik.electronics.store.product.service.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(
    description = "Доступные варианты сортировки"
)
@Getter
@RequiredArgsConstructor
public enum SortByOptions {
    NAME("name", "По названию"),
    PRICE("price", "По стоимости");

    private final String databaseFieldName;
    private final String description;
}