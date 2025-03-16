package ru.edalik.electronics.store.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import ru.edalik.electronics.store.product.service.model.entity.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>,
    ListPagingAndSortingRepository<Product, UUID>,
    JpaSpecificationExecutor<Product> {

    @Modifying
    @Query("DELETE Product p WHERE p.id = :id")
    int customDeleteById(UUID id);

    @Modifying
    @Query("UPDATE Product p SET p.quantity = p.quantity - :quantity WHERE p.id = :id")
    int decreaseQuantity(Integer quantity, UUID id);

}