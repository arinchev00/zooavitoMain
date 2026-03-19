package com.example.zooavito.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление порядка категорий")
public class CategoryOrderRequest {

    @NotNull(message = "ID категории обязателен")
    @Schema(description = "ID категории", example = "1")
    private Long id;

    @NotNull(message = "Порядок отображения обязателен")
    @Schema(description = "Порядок отображения (чем меньше число, тем выше позиция)", example = "0")
    private Integer order;
}