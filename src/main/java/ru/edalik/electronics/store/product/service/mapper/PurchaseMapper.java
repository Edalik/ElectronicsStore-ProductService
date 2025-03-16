package ru.edalik.electronics.store.product.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.edalik.electronics.store.product.service.model.dto.PurchaseDto;
import ru.edalik.electronics.store.product.service.model.entity.Purchase;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PurchaseMapper {

    Purchase toEntity(PurchaseDto basketDto);

    List<Purchase> toEntity(List<PurchaseDto> basketDtos);

    PurchaseDto toDto(Purchase basket);

    List<PurchaseDto> toDto(List<Purchase> baskets);

}