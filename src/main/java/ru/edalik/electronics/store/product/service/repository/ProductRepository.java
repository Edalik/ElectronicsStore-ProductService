package ru.edalik.electronics.store.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import ru.edalik.electronics.store.product.service.model.entity.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, ListPagingAndSortingRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

}