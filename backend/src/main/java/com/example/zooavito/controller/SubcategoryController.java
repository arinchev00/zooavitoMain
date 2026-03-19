package com.example.zooavito.controller;

import com.example.zooavito.config.ApiResponseAnnotations;
import com.example.zooavito.request.SubcategoryRequest;
import com.example.zooavito.response.SubcategoryResponse;
import com.example.zooavito.service.Subcategory.SubcategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/subcategories")
@RequiredArgsConstructor
@Tag(name = "Подкатегории", description = "API для работы с подкатегориями (только ADMIN)")
public class SubcategoryController {

    private final SubcategoryService subcategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание новой подкатегории (ADMIN)")
    @ApiResponse(responseCode = "201", description = "Подкатегория успешно создана")
    @ApiResponseAnnotations.CommonPostResponses
    public SubcategoryResponse createSubcategory(@Valid @RequestBody SubcategoryRequest request) {
        return subcategoryService.createSubcategory(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить подкатегорию по id")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = SubcategoryResponse.class)))
    @ApiResponseAnnotations.CommonGetResponses
    public SubcategoryResponse getSubcategory(@PathVariable Long id) {
        return subcategoryService.getSubcategoryById(id);
    }

    @GetMapping("/by-category/{categoryId}")
    @Operation(summary = "Получить подкатегории категории")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = SubcategoryResponse.class)))
    @ApiResponseAnnotations.CommonGetResponses
    public List<SubcategoryResponse> getSubcategoriesByCategory(@PathVariable Long categoryId) {
        return subcategoryService.getSubcategoriesByCategoryId(categoryId);
    }

    @GetMapping("/by-category/{categoryId}/paged")
    @Operation(summary = "Получить подкатегории категории с пагинацией")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponseAnnotations.CommonGetResponses
    public Page<SubcategoryResponse> getSubcategoriesByCategoryPaged(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return subcategoryService.getSubcategoriesByCategoryId(categoryId, pageable);
    }

    @GetMapping
    @Operation(summary = "Получить все подкатегории")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = SubcategoryResponse.class)))
    @ApiResponseAnnotations.CommonGetResponses
    public List<SubcategoryResponse> getAllSubcategories() {
        return subcategoryService.getAllSubcategories();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить подкатегорию (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Подкатегория обновлена",
            content = @Content(schema = @Schema(implementation = SubcategoryResponse.class)))
    @ApiResponseAnnotations.CommonPostResponses
    @ApiResponseAnnotations.NotFoundResponse
    public SubcategoryResponse updateSubcategory(
            @PathVariable Long id,
            @Valid @RequestBody SubcategoryRequest request
    ) {
        return subcategoryService.updateSubcategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить подкатегорию (ADMIN)")
    @ApiResponse(responseCode = "204", description = "Подкатегория удалена", content = @Content)
    @ApiResponseAnnotations.CommonPostResponses
    @ApiResponseAnnotations.NotFoundResponse
    public void deleteSubcategory(@PathVariable Long id) {
        subcategoryService.deleteSubcategory(id);
    }
}
