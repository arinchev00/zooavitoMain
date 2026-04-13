package com.example.zooavito.controller;

import com.example.zooavito.config.ApiResponseAnnotations;
import com.example.zooavito.request.CommentRequest;
import com.example.zooavito.response.CommentResponse;
import com.example.zooavito.service.Comment.CommentService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/comments")
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "API для работы с комментариями")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    // Проверка аутентификации
    private void checkAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("Попытка доступа к защищенному эндпоинту без аутентификации");
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }
    }

    @PostMapping("/announcement/{announcementId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить комментарий к объявлению")
    @ApiResponse(responseCode = "201", description = "Комментарий создан")
    @ApiResponseAnnotations.CommonPostResponses
    public CommentResponse createComment(
            @PathVariable Long announcementId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        checkAuthentication(authentication);

        String userEmail = authentication.getName();
        log.info("Создание комментария к объявлению ID: {}, пользователь: {}", announcementId, userEmail);
        return commentService.createComment(announcementId, request, userEmail);
    }

    @GetMapping("/announcement/{announcementId}")
    @Operation(summary = "Получить все комментарии к объявлению")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = CommentResponse.class)))
    @ApiResponseAnnotations.NotFoundResponse
    @ApiResponseAnnotations.InternalServerErrorResponse
    public List<CommentResponse> getCommentsByAnnouncement(@PathVariable Long announcementId) {
        // Публичный эндпоинт - не требует аутентификации
        return commentService.getCommentsByAnnouncementId(announcementId);
    }

    @GetMapping("/announcement/{announcementId}/paged")
    @Operation(summary = "Получить комментарии к объявлению с пагинацией")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = CommentResponse.class)))
    @ApiResponseAnnotations.CommonGetResponses
    @ApiResponseAnnotations.NotFoundResponse
    public Page<CommentResponse> getCommentsByAnnouncementPaged(
            @PathVariable Long announcementId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // Публичный эндпоинт - не требует аутентификации
        return commentService.getCommentsByAnnouncementId(announcementId, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить комментарий по ID")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = CommentResponse.class)))
    @ApiResponseAnnotations.NotFoundResponse
    @ApiResponseAnnotations.InternalServerErrorResponse
    public CommentResponse getComment(@PathVariable Long id) {
        // Публичный эндпоинт - не требует аутентификации
        return commentService.getCommentById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить комментарий")
    @ApiResponse(responseCode = "200", description = "Комментарий обновлен",
            content = @Content(schema = @Schema(implementation = CommentResponse.class)))
    @ApiResponseAnnotations.CommonPutResponses
    public CommentResponse updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        checkAuthentication(authentication);

        String userEmail = authentication.getName();
        log.info("Обновление комментария ID: {}, пользователь: {}", id, userEmail);
        return commentService.updateComment(id, request, userEmail);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить комментарий")
    @ApiResponse(responseCode = "204", description = "Комментарий удален")
    @ApiResponseAnnotations.CommonDeleteResponses
    public void deleteComment(
            @PathVariable Long id,
            Authentication authentication
    ) {
        checkAuthentication(authentication);

        String userEmail = authentication.getName();
        log.info("Удаление комментария ID: {}, пользователь: {}", id, userEmail);
        commentService.deleteComment(id, userEmail);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить комментарии пользователя")
    @ApiResponse(responseCode = "200", description = "Успешно",
            content = @Content(schema = @Schema(implementation = CommentResponse.class)))
    @ApiResponseAnnotations.NotFoundResponse
    @ApiResponseAnnotations.InternalServerErrorResponse
    public List<CommentResponse> getCommentsByUser(@PathVariable Long userId) {
        // Публичный эндпоинт - не требует аутентификации
        return commentService.getCommentsByUserId(userId);
    }
}