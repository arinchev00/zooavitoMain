package com.example.zooavito.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание/обновление категории")
public class CategoryRequest {

    @NotBlank(message = "Название категории обязательно")
    @Schema(description = "Название категории", example = "Кошки")
    private String title;
}
