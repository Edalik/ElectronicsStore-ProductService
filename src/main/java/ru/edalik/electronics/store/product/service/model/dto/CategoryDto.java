package ru.edalik.electronics.store.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
@Schema(description = "DTO для представления категории")
public record CategoryDto(
    @Schema(
        description = "Название категории",
        example = "Категория"
    )
    @NotBlank @Length(min = 1, max = 50) String name
) {

}