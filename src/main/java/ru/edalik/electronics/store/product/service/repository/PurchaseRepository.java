package ru.edalik.electronics.store.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edalik.electronics.store.product.service.model.entity.Purchase;

import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

}