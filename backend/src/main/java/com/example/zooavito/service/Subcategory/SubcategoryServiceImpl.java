package com.example.zooavito.service.Subcategory;

import com.example.zooavito.model.Category;
import com.example.zooavito.model.Subcategory;
import com.example.zooavito.repository.CategoryRepository;
import com.example.zooavito.repository.SubcategoryRepository;
import com.example.zooavito.request.SubcategoryRequest;
import com.example.zooavito.response.SubcategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubcategoryServiceImpl implements SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public SubcategoryResponse createSubcategory(SubcategoryRequest request) {
        log.info("=== СОЗДАНИЕ ПОДКАТЕГОРИИ ===");
        log.info("Название: {}, категория ID: {}", request.getTitle(), request.getCategoryId());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Категория не найдена с id: " + request.getCategoryId()
                ));

        boolean exists = subcategoryRepository.existsByTitleAndCategoryId(request.getTitle(), request.getCategoryId());
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Подкатегория с названием '" + request.getTitle() + "' уже существует в этой категории"
            );
        }

        Subcategory subcategory = new Subcategory();
        subcategory.setTitle(request.getTitle());
        subcategory.setCategory(category);

        Subcategory savedSubcategory = subcategoryRepository.save(subcategory);
        log.info("✅ Подкатегория создана с ID: {}", savedSubcategory.getId());

        return SubcategoryResponse.from(savedSubcategory);
    }

    @Override
    @Transactional(readOnly = true)
    public SubcategoryResponse getSubcategoryById(Long id) {
        log.info("=== ПОЛУЧЕНИЕ ПОДКАТЕГОРИИ ID: {} ===", id);

        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Подкатегория не найдена с id: " + id
                ));

        return SubcategoryResponse.from(subcategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoryResponse> getSubcategoriesByCategoryId(Long categoryId) {
        log.info("=== ПОЛУЧЕНИЕ ПОДКАТЕГОРИЙ ДЛЯ КАТЕГОРИИ ID: {} ===", categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Категория не найдена с id: " + categoryId
            );
        }

        List<Subcategory> subcategories = subcategoryRepository.findByCategoryIdOrderByTitleAsc(categoryId);
        log.info("Найдено подкатегорий: {}", subcategories.size());

        return subcategories.stream()
                .map(SubcategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubcategoryResponse> getSubcategoriesByCategoryId(Long categoryId, Pageable pageable) {
        // Проверяем существование категории
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Категория не найдена с id: " + categoryId
            );
        }

        return subcategoryRepository.findByCategoryId(categoryId, pageable)
                .map(SubcategoryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoryResponse> getAllSubcategories() {
        log.info("=== ПОЛУЧЕНИЕ ВСЕХ ПОДКАТЕГОРИЙ ===");

        return subcategoryRepository.findAllByOrderByCategoryTitleAscTitleAsc()
                .stream()
                .map(SubcategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubcategoryResponse updateSubcategory(Long id, SubcategoryRequest request) {
        log.info("=== ОБНОВЛЕНИЕ ПОДКАТЕГОРИИ ID: {} ===", id);

        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Подкатегория не найдена с id: " + id
                ));

        if (!subcategory.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Категория не найдена с id: " + request.getCategoryId()
                    ));
            subcategory.setCategory(newCategory);
        }

        if (!subcategory.getTitle().equals(request.getTitle())) {
            boolean exists = subcategoryRepository.existsByTitleAndCategoryId(
                    request.getTitle(),
                    subcategory.getCategory().getId()
            );
            if (exists) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Подкатегория с названием '" + request.getTitle() + "' уже существует в этой категории"
                );
            }
            subcategory.setTitle(request.getTitle());
        }

        Subcategory updatedSubcategory = subcategoryRepository.save(subcategory);
        log.info("✅ Подкатегория обновлена");

        return SubcategoryResponse.from(updatedSubcategory);
    }

    @Override
    @Transactional
    public void deleteSubcategory(Long id) {
        log.info("=== УДАЛЕНИЕ ПОДКАТЕГОРИИ ID: {} ===", id);

        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Подкатегория не найдена с id: " + id
                ));

        if (subcategory.getAnnouncements() != null && !subcategory.getAnnouncements().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Нельзя удалить подкатегорию, которая используется в объявлениях"
            );
        }

        subcategoryRepository.delete(subcategory);
        log.info("✅ Подкатегория удалена");
    }
}
