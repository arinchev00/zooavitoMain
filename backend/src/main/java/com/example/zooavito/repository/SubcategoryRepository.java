package com.example.zooavito.repository;

import com.example.zooavito.model.Subcategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

    // Найти все подкатегории, отсортированные по порядку
    List<Subcategory> findByCategoryIdOrderByTitleAsc(Long categoryId);

    // С пагинацией
    Page<Subcategory> findByCategoryId(Long categoryId, Pageable pageable);

    boolean existsByTitleAndCategoryId(String title, Long categoryId);

    // Найти максимальное значение порядка для новой подкатегории
    @Query("SELECT s FROM Subcategory s JOIN s.category c ORDER BY c.title ASC, s.title ASC")
    List<Subcategory> findAllByOrderByCategoryTitleAscTitleAsc();
}
