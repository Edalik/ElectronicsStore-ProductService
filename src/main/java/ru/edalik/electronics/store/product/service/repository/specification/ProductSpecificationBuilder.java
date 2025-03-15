package ru.edalik.electronics.store.product.service.repository.specification;

import jakarta.persistence.criteria.Join;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.edalik.electronics.store.product.service.model.dto.CategoryDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductSpecificationDto;
import ru.edalik.electronics.store.product.service.model.entity.Category;
import ru.edalik.electronics.store.product.service.model.entity.Product;

import java.math.BigDecimal;

@Component
public class ProductSpecificationBuilder {

    public Specification<Product> getSpecification(ProductSpecificationDto specificationDto) {
        return Specification
            .where(hasName(specificationDto.name()))
            .and(hasCategory(specificationDto.category()))
            .and(minPrice(specificationDto.minPrice()))
            .and(maxPrice(specificationDto.maxPrice()));
    }

    public Pageable getPageable(ProductSpecificationDto specificationDto) {
        Sort sort = Sort.by(specificationDto.sortDirection(), specificationDto.sortBy().getDatabaseFieldName());

        return PageRequest.of(specificationDto.page(), specificationDto.size(), sort);
    }

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) ->
            StringUtils.isBlank(name) ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Product> hasCategory(CategoryDto category) {
        return (root, query, cb) -> {
            if (category == null || StringUtils.isBlank(category.name())) return null;

            Join<Product, Category> categoryJoin = root.join("category");
            return cb.equal(categoryJoin.get("name"), category.name());
        };
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, cb) ->
            minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, cb) ->
            maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

}