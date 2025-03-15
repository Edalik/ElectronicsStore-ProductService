package ru.edalik.electronics.store.product.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.edalik.electronics.store.product.service.model.dto.CategoryDto;
import ru.edalik.electronics.store.product.service.model.entity.Category;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    Category toEntity(CategoryDto categoryDto);

    CategoryDto toDto(Category category);

}