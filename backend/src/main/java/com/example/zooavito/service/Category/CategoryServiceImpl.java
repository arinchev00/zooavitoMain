package com.example.zooavito.service.Category;

import com.example.zooavito.model.Category;
import com.example.zooavito.repository.CategoryRepository;
import com.example.zooavito.request.CategoryOrderRequest;
import com.example.zooavito.request.CategoryRequest;
import com.example.zooavito.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("=== СОЗДАНИЕ КАТЕГОРИИ ===");
        log.info("Название: {}", request.getTitle());

        if (categoryRepository.findByTitle(request.getTitle()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Категория с названием '" + request.getTitle() + "' уже существует"
            );
        }

        Category category = new Category();
        category.setTitle(request.getTitle());

        Integer maxOrder = categoryRepository.findMaxDisplayOrder();
        category.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 0);

        Category savedCategory = categoryRepository.save(category);
        log.info("✅ Категория создана с ID: {}, порядок: {}", savedCategory.getId(), savedCategory.getDisplayOrder());

        return CategoryResponse.from(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Категория не найдена с id: " + id
                ));

        return CategoryResponse.from(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAllByIsHiddenFalseOrderByDisplayOrderAsc();
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategoriesForAdmin() {
        List<Category> categories = categoryRepository.findAllByOrderByDisplayOrderAsc();
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("=== ОБНОВЛЕНИЕ КАТЕГОРИИ С ID: {} ===", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Категория не найдена с id: " + id
                ));

        categoryRepository.findByTitle(request.getTitle())
                .ifPresent(existingCategory -> {
                    if (!existingCategory.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Категория с названием '" + request.getTitle() + "' уже существует"
                        );
                    }
                });

        category.setTitle(request.getTitle());
        Category updatedCategory = categoryRepository.save(category);

        log.info("✅ Категория обновлена");
        return CategoryResponse.from(updatedCategory);  // ← теперь возвращает и id
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Категория не найдена с id: " + id
                ));

        if (!category.getAnnouncements().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Нельзя удалить категорию, которая используется в объявлениях"
            );
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public void updateCategoryOrder(List<CategoryOrderRequest> orderRequests) {
        log.info("=== ОБНОВЛЕНИЕ ПОРЯДКА КАТЕГОРИЙ ===");

        for (CategoryOrderRequest request : orderRequests) {
            Category category = categoryRepository.findById(request.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Категория не найдена с id: " + request.getId()
                    ));

            category.setDisplayOrder(request.getOrder());
            categoryRepository.save(category);
            log.debug("Категория ID: {}, новый порядок: {}", request.getId(), request.getOrder());
        }

        log.info("✅ Порядок категорий успешно обновлен");
    }

    private void reorderRemainingCategories() {
        List<Category> categories = categoryRepository.findAllByOrderByDisplayOrderAsc();
        int order = 0;
        for (Category category : categories) {
            category.setDisplayOrder(order++);
            categoryRepository.save(category);
        }
    }

    @Override
    @Transactional
    public void hideCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Категория не найдена с id: " + id
                ));
        category.setIsHidden(true);
        categoryRepository.save(category);
        log.info("Категория {} скрыта", id);
    }

    @Override
    @Transactional
    public void showCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Категория не найдена с id: " + id
                ));
        category.setIsHidden(false);
        categoryRepository.save(category);
        log.info("Категория {} показана", id);
    }
}
