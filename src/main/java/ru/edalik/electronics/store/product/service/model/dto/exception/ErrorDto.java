package ru.edalik.electronics.store.product.service.model.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
@Schema(description = "Объект ошибки API")
public record ErrorDto(
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
        example = "Bad Request"
    )
    String error,

    @Schema(
        description = "Сообщение об ошибке",
        example = "Invalid request parameters"
    )
    String message,

    @Schema(
        description = "Путь запроса",
        example = "/api/v1/users"
    )
    String path
) {

}