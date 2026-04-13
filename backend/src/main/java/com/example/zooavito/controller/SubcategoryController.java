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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/subcategories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Подкатегории", description = "API для работы с подкатегориями (только ADMIN)")
public class SubcategoryController {

    private final SubcategoryService subcategoryService;

    // Проверка администратора (вынесем в отдельный метод для DRY)
    private void checkAdminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Проверка аутентификации
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            log.warn("Попытка доступа к админскому эндпоинту без аутентификации");
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        // Проверка роли ADMIN
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            log.warn("Пользователь {} пытается выполнить админскую операцию без прав администратора", auth.getName());
            throw new AccessDeniedException("Недостаточно прав для выполнения операции");
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание новой подкатегории (ADMIN)")
    @ApiResponse(responseCode = "201", description = "Подкатегория успешно создана")
    @ApiResponseAnnotations.CommonPostAdminResponses
    public SubcategoryResponse createSubcategory(@Valid @RequestBody SubcategoryRequest request) {
        checkAdminAccess();
        log.info("Создание новой подкатегории: {}", request.getTitle());
        return subcategoryService.createSubcategory(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить подкатегорию по id")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = SubcategoryResponse.class)))
    @ApiResponseAnnotations.NotFoundResponse
    @ApiResponseAnnotations.InternalServerErrorResponse
    public SubcategoryResponse getSubcategory(@PathVariable Long id) {
        // Публичный эндпоинт - не требует аутентификации
        return subcategoryService.getSubcategoryById(id);
    }

    @GetMapping("/by-category/{categoryId}")
    @Operation(summary = "Получить подкатегории категории")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = SubcategoryResponse.class)))
    @ApiResponseAnnotations.NotFoundResponse
    @ApiResponseAnnotations.InternalServerErrorResponse
    public List<SubcategoryResponse> getSubcategoriesByCategory(@PathVariable Long categoryId) {
        // Публичный эндпоинт - не требует аутентификации
        return subcategoryService.getSubcategoriesByCategoryId(categoryId);
    }

    @GetMapping("/by-category/{categoryId}/paged")
    @Operation(summary = "Получить подкатегории категории с пагинацией")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponseAnnotations.CommonGetResponses
    @ApiResponseAnnotations.NotFoundResponse
    public Page<SubcategoryResponse> getSubcategoriesByCategoryPaged(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        // Публичный эндпоинт - не требует аутентификации
        return subcategoryService.getSubcategoriesByCategoryId(categoryId, pageable);
    }

    @GetMapping
    @Operation(summary = "Получить все подкатегории")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = SubcategoryResponse.class)))
    @ApiResponseAnnotations.InternalServerErrorResponse
    public List<SubcategoryResponse> getAllSubcategories() {
        // Публичный эндпоинт - не требует аутентификации
        return subcategoryService.getAllSubcategories();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить подкатегорию (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Подкатегория обновлена",
            content = @Content(schema = @Schema(implementation = SubcategoryResponse.class)))
    @ApiResponseAnnotations.CommonPutResponses
    public SubcategoryResponse updateSubcategory(
            @PathVariable Long id,
            @Valid @RequestBody SubcategoryRequest request
    ) {
        checkAdminAccess();
        log.info("Обновление подкатегории: id={}, title={}", id, request.getTitle());
        return subcategoryService.updateSubcategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить подкатегорию (ADMIN)")
    @ApiResponse(responseCode = "204", description = "Подкатегория удалена", content = @Content)
    @ApiResponseAnnotations.CommonDeleteResponses
    public void deleteSubcategory(@PathVariable Long id) {
        checkAdminAccess();
        log.info("Удаление подкатегории: id={}", id);
        subcategoryService.deleteSubcategory(id);
    }
}