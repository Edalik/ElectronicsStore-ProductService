package ru.edalik.electronics.store.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edalik.electronics.store.product.service.model.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {

}