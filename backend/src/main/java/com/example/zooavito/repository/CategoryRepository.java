package com.example.zooavito.repository;

import com.example.zooavito.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByTitle(String title);

    // Все категории, отсортированные по порядку
    List<Category> findAllByOrderByDisplayOrderAsc();

    // Только видимые категории (isHidden = false), отсортированные по порядку
    List<Category> findAllByIsHiddenFalseOrderByDisplayOrderAsc();

    // Найти максимальное значение порядка для новой категории
    @Query("SELECT MAX(c.displayOrder) FROM Category c")
    Integer findMaxDisplayOrder();
}