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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @ApiResponseAnnotations.CommonGetAdminResponses
    public List<CategoryResponse> getAllCategoriesForAdmin() {
        // Получаем текущего пользователя
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Если пользователь не аутентифицирован
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            log.warn("Попытка доступа к админским категориям без аутентификации");
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        // Проверяем роль ADMIN
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            log.warn("Пользователь {} пытается получить админские категории без прав администратора", auth.getName());
            throw new AccessDeniedException("Недостаточно прав для выполнения операции");
        }

        log.info("Запрос всех категорий для админ-панели от пользователя: {}", auth.getName());
        return categoryService.getAllCategoriesForAdmin();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание новой категории (ADMIN)")
    @ApiResponse(responseCode = "201", description = "Категория успешно создана")
    @ApiResponseAnnotations.CommonPostAdminResponses
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        // Аналогичная проверка
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Недостаточно прав для выполнения операции");
        }

        return categoryService.createCategory(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить категорию по id")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    @ApiResponseAnnotations.NotFoundResponse
    @ApiResponseAnnotations.InternalServerErrorResponse
    public CategoryResponse getCategory(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping
    @Operation(summary = "Получить все категории (только видимые)")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    @ApiResponseAnnotations.InternalServerErrorResponse
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PutMapping("/order")
    @Operation(summary = "Обновить порядок отображения категорий")
    @ApiResponse(responseCode = "200", description = "Порядок категорий успешно сохранен")
    @ApiResponseAnnotations.CommonPostResponses
    @ApiResponseAnnotations.ForbiddenResponse
    public void updateCategoryOrder(@RequestBody List<CategoryOrderRequest> orderRequests) {
        // Проверка аутентификации и роли
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Недостаточно прав для выполнения операции");
        }

        log.info("Обновление порядка категорий");
        categoryService.updateCategoryOrder(orderRequests);
    }

    @PutMapping("/{id}/hide")
    @Operation(summary = "Скрыть категорию")
    @ApiResponse(responseCode = "200", description = "Категория скрыта")
    @ApiResponseAnnotations.CommonPutResponses
    public void hideCategory(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Недостаточно прав для выполнения операции");
        }

        log.info("Скрытие категории с ID: {}", id);
        categoryService.hideCategory(id);
    }

    @PutMapping("/{id}/show")
    @Operation(summary = "Показать категорию")
    @ApiResponse(responseCode = "200", description = "Категория показана")
    @ApiResponseAnnotations.CommonPutResponses
    public void showCategory(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Недостаточно прав для выполнения операции");
        }

        log.info("Отображение категории с ID: {}", id);
        categoryService.showCategory(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить категорию (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Категория обновлена",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    @ApiResponseAnnotations.CommonPutResponses
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Недостаточно прав для выполнения операции");
        }

        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить категорию (ADMIN)")
    @ApiResponse(responseCode = "204", description = "Категория удалена", content = @Content)
    @ApiResponseAnnotations.CommonDeleteResponses
    public void deleteCategory(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Недостаточно прав для выполнения операции");
        }

        categoryService.deleteCategory(id);
    }
}