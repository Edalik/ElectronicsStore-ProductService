package ru.edalik.electronics.store.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edalik.electronics.store.product.service.model.entity.Basket;

public interface BasketRepository extends JpaRepository<Basket, Basket.BasketId> {

}