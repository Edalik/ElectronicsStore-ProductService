package ru.edalik.electronics.store.product.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.edalik.electronics.store.product.service.model.dto.BasketDto;
import ru.edalik.electronics.store.product.service.model.entity.Basket;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BasketMapper {

    Basket toEntity(BasketDto basketDto);

    List<Basket> toEntity(List<BasketDto> basketDtos);

    @Mapping(target = "product", source = "compositeKey.product")
    BasketDto toDto(Basket basket);

    List<BasketDto> toDto(List<Basket> baskets);

}