package com.example.zooavito.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@Schema(description = "Запрос на создание объявления")
public class AnnouncementRequest {

    @NotBlank(message = "Заголовок обязателен")
    @Schema(description = "Заголовок объявления", example = "Продаю кота")
    private String title;

    @NotNull(message = "Цена обязательна")
    @Min(value = 0, message = "Цена не может быть отрицательной")
    @Schema(description = "Цена", example = "1000")
    private int price;

    @Schema(description = "Описание", example = "К лотку приучен")
    private String description;

    @NotNull(message = "Подкатегория обязательна")
    @Schema(description = "ID подкатегории", example = "1")
    private Long subcategoryId;  //
}
