package ru.edalik.electronics.store.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.edalik.electronics.store.product.service.model.entity.Purchase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

    @Query("SELECT p FROM Purchase p WHERE p.id = :id AND p.userId = :userId")
    Optional<Purchase> findById(UUID id, UUID userId);

    List<Purchase> findByUserId(UUID userId);

}