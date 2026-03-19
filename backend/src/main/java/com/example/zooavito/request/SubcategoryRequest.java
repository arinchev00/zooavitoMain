package com.example.zooavito.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание/обновление подкатегории")
public class SubcategoryRequest {

    @NotBlank(message = "Название подкатегории обязательно")
    @Schema(description = "Название подкатегории", example = "Британские кошки")
    private String title;

    @NotNull(message = "ID категории обязателен")
    @Schema(description = "ID родительской категории", example = "1")
    private Long categoryId;
}
