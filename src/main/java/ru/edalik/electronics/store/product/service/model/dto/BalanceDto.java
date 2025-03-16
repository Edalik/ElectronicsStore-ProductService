package ru.edalik.electronics.store.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO для взаимодействия с счетом пользователя")
public record BalanceDto(
    @Schema(
        description = "Сумма",
        example = "123.45"
    )
    BigDecimal amount
) {

}