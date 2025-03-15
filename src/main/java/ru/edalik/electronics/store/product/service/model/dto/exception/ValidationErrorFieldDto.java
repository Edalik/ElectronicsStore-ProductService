package ru.edalik.electronics.store.product.service.model.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Информация об ошибке валидации поля")
public record ValidationErrorFieldDto(
    @Schema(
        description = "Название поля с ошибкой",
        example = "email"
    )
    String field,

    @Schema(
        description = "Сообщение об ошибке",
        example = "Должно быть действительным email адресом"
    )
    String message
) {

}