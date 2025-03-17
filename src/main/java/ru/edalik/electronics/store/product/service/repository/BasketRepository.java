package ru.edalik.electronics.store.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.edalik.electronics.store.product.service.model.entity.Basket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BasketRepository extends JpaRepository<Basket, Basket.BasketId> {

    @Query("SELECT b FROM Basket b WHERE b.compositeKey.userId = :userId")
    List<Basket> findByUserId(UUID userId);

    @Query("SELECT b FROM Basket b WHERE b.compositeKey.userId = :userId AND b.compositeKey.product.id = :productId")
    Optional<Basket> findByCompositeKey(UUID userId, UUID productId);

    @Modifying
    @Query("delete from Basket b where b.compositeKey.userId = :userId and  b.compositeKey.product.id = :productId")
    int deleteByCompositeKey(UUID userId, UUID productId);

}