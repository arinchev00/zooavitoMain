package com.example.zooavito.controller;

import com.example.zooavito.config.ApiResponseAnnotations;
import com.example.zooavito.request.CategoryOrderRequest;
import com.example.zooavito.request.CategoryRequest;
import com.example.zooavito.response.CategoryResponse;
import com.example.zooavito.service.Category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Категории", description = "API для работы с категориями")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/admin")
    @Operation(summary = "Получить все категории для админ-панели (включая скрытые)")
    @ApiResponse(responseCode = "200", description = "Успешно")
    public List<CategoryResponse> getAllCategoriesForAdmin() {
        log.info("Запрос всех категорий для админ-панели");
        return categoryService.getAllCategoriesForAdmin();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание новой категории (ADMIN)")
    @ApiResponse(responseCode = "201", description = "Категория успешно создана")
    @ApiResponseAnnotations.CommonPostResponses
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить категорию по id")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    @ApiResponseAnnotations.CommonGetResponses
    public CategoryResponse getCategory(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping
    @Operation(summary = "Получить все категории (только видимые)")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    @ApiResponseAnnotations.CommonGetResponses
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PutMapping("/order")
    @Operation(summary = "Обновить порядок отображения категорий")
    @ApiResponse(responseCode = "200", description = "Порядок категорий успешно сохранен")
    @ApiResponseAnnotations.CommonPostResponses
    public void updateCategoryOrder(@RequestBody List<CategoryOrderRequest> orderRequests) {
        log.info("Обновление порядка категорий");
        categoryService.updateCategoryOrder(orderRequests);
    }

    @PutMapping("/{id}/hide")
    @Operation(summary = "Скрыть категорию")
    @ApiResponse(responseCode = "200", description = "Категория скрыта")
    public void hideCategory(@PathVariable Long id) {
        log.info("Скрытие категории с ID: {}", id);
        categoryService.hideCategory(id);
    }

    @PutMapping("/{id}/show")
    @Operation(summary = "Показать категорию")
    @ApiResponse(responseCode = "200", description = "Категория показана")
    public void showCategory(@PathVariable Long id) {
        log.info("Отображение категории с ID: {}", id);
        categoryService.showCategory(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить категорию (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Категория обновлена",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    @ApiResponseAnnotations.CommonPostResponses
    @ApiResponseAnnotations.NotFoundResponse
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить категорию (ADMIN)")
    @ApiResponse(responseCode = "204", description = "Категория удалена", content = @Content)
    @ApiResponseAnnotations.CommonPostResponses
    @ApiResponseAnnotations.NotFoundResponse
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}