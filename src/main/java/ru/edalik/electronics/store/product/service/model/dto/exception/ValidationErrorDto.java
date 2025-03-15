package ru.edalik.electronics.store.product.service.model.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
@Schema(description = "Объект ошибки валидации")
public record ValidationErrorDto(
    @Schema(
        description = "Дата и время возникновения ошибки",
        example = "2024-02-20T14:30:45.123+03:00"
    )
    ZonedDateTime timestamp,

    @Schema(
        description = "HTTP статус код",
        example = "400"
    )
    int status,

    @Schema(
        description = "Тип ошибки",
        example = "Validation Error"
    )
    String error,

    @Schema(
        description = "Список ошибок валидации"
    )
    List<ValidationErrorFieldDto> fields,

    @Schema(
        description = "Путь запроса",
        example = "/api/v1/users"
    )
    String path
) {

}