package com.example.zooavito.controller;

import com.example.zooavito.config.ApiResponseAnnotations;
import com.example.zooavito.request.AnnouncementRequest;
import com.example.zooavito.response.AnnouncementResponse;
import com.example.zooavito.service.Announcement.AnnouncementService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно",
                    content = @Content(schema = @Schema(implementation = AnnouncementResponse.class)))
    })
    @ApiResponseAnnotations.CommonGetResponses
    public List<AnnouncementResponse> getAllAnnouncements(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subcategoryId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice
    ) {
        log.info("Получение всех объявлений с фильтрацией: categoryId={}, subcategoryId={}, minPrice={}, maxPrice={}",
                categoryId, subcategoryId, minPrice, maxPrice);
        return announcementService.getAllAnnouncements(categoryId, subcategoryId, minPrice, maxPrice);
    }

    @GetMapping("/user/me")
    @Operation(summary = "Получить объявления текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно",
                    content = @Content(schema = @Schema(implementation = AnnouncementResponse.class)))
    })
    @ApiResponseAnnotations.CommonGetResponses
    public List<AnnouncementResponse> getMyAnnouncements(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        log.info("Получение объявлений пользователя: {}", userEmail);
        return announcementService.getAnnouncementsByUserEmail(userEmail);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание нового объявления (можно несколько фото)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Объявление успешно создано")
    })
    @ApiResponseAnnotations.CommonPostResponses
    @ApiResponseAnnotations.UnsupportedMediaResponse
    public AnnouncementResponse createAnnouncement(
            @Valid @RequestPart("announcement") AnnouncementRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,  // ← List!
            Authentication authentication
    ) throws IOException {
        String userEmail = authentication.getName();
        log.info("Создание объявления: title={}, imagesCount={}, user={}",
                request.getTitle(), images != null ? images.size() : 0, userEmail);
        return announcementService.createAnnouncement(request, images, userEmail);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить объявление по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно",
                    content = @Content(schema = @Schema(implementation = AnnouncementResponse.class)))
    })
    @ApiResponseAnnotations.CommonGetResponses
    public AnnouncementResponse getAnnouncement(@PathVariable Long id) {
        return announcementService.getAnnouncementById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить объявление (можно несколько фото)")
    public AnnouncementResponse updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestPart("announcement") AnnouncementRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "imagesToDelete", required = false) String imagesToDeleteJson,
            @RequestPart(value = "mainImageId", required = false) String mainImageIdStr,  // добавить!
            Authentication authentication
    ) throws IOException {
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

        log.info("Обновление объявления: id={}, imagesCount={}, imagesToDelete={}, mainImageId={}, user={}",
                id, images != null ? images.size() : 0,
                imagesToDelete != null ? imagesToDelete.size() : 0,
                mainImageId, userEmail);

        return announcementService.updateAnnouncement(id, request, images, imagesToDelete, mainImageId, userEmail);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить объявление")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Объявление удалено")
    })
    @ApiResponseAnnotations.CommonPostResponses
    @ApiResponseAnnotations.NotFoundResponse
    public void deleteAnnouncement(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        log.info("Удаление объявления: id={}, user={}", id, userEmail);
        announcementService.deleteAnnouncement(id, userEmail);
    }
}