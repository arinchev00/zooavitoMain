package com.example.zooavito.controller;

import com.example.zooavito.config.ApiResponseAnnotations;
import com.example.zooavito.request.AnnouncementRequest;
import com.example.zooavito.response.AnnouncementResponse;
import com.example.zooavito.service.Announcement.AnnouncementService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/api/announcement")
@RequiredArgsConstructor
@Tag(name = "Объявления", description = "API для работы с объявлениями")
@Slf4j
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    @Operation(summary = "Получить все объявления с фильтрацией")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponseAnnotations.CommonGetResponses
    public List<AnnouncementResponse> getAllAnnouncements(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subcategoryId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice
    ) {
        log.info("Получение всех объявлений с фильтрацией");
        return announcementService.getAllAnnouncements(categoryId, subcategoryId, minPrice, maxPrice);
    }

    @GetMapping("/user/me")
    @Operation(summary = "Получить объявления текущего пользователя")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponseAnnotations.CommonGetResponses
    @ApiResponseAnnotations.UnauthorizedResponse
    public List<AnnouncementResponse> getMyAnnouncements(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        String userEmail = authentication.getName();
        log.info("Получение объявлений пользователя: {}", userEmail);
        return announcementService.getAnnouncementsByUserEmail(userEmail);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание нового объявления (можно несколько фото)")
    @ApiResponse(responseCode = "201", description = "Объявление успешно создано")
    @ApiResponseAnnotations.CommonPostResponses
    public AnnouncementResponse createAnnouncement(
            @Valid @RequestPart("announcement") AnnouncementRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication
    ) throws IOException {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        String userEmail = authentication.getName();
        log.info("Создание объявления: title={}, user={}", request.getTitle(), userEmail);
        return announcementService.createAnnouncement(request, images, userEmail);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить объявление по id")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponseAnnotations.NotFoundResponse
    @ApiResponseAnnotations.InternalServerErrorResponse
    public AnnouncementResponse getAnnouncement(@PathVariable Long id) {
        return announcementService.getAnnouncementById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить объявление (можно несколько фото)")
    @ApiResponse(responseCode = "200", description = "Объявление обновлено")
    @ApiResponseAnnotations.CommonPutResponses
    public AnnouncementResponse updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestPart("announcement") AnnouncementRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "imagesToDelete", required = false) String imagesToDeleteJson,
            @RequestPart(value = "mainImageId", required = false) String mainImageIdStr,
            Authentication authentication
    ) throws IOException {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        String userEmail = authentication.getName();

        List<Long> imagesToDelete = null;
        if (imagesToDeleteJson != null && !imagesToDeleteJson.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            imagesToDelete = mapper.readValue(imagesToDeleteJson, new TypeReference<List<Long>>() {});
        }

        Long mainImageId = null;
        if (mainImageIdStr != null && !mainImageIdStr.isEmpty()) {
            mainImageId = Long.parseLong(mainImageIdStr);
        }

        log.info("Обновление объявления: id={}, user={}", id, userEmail);
        return announcementService.updateAnnouncement(id, request, images, imagesToDelete, mainImageId, userEmail);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить объявление")
    @ApiResponse(responseCode = "204", description = "Объявление удалено")
    @ApiResponseAnnotations.CommonDeleteResponses
    public void deleteAnnouncement(
            @PathVariable Long id,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не авторизован");
        }

        String userEmail = authentication.getName();
        log.info("Удаление объявления: id={}, user={}", id, userEmail);
        announcementService.deleteAnnouncement(id, userEmail);
    }
}