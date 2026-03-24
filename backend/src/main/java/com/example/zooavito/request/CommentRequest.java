package com.example.zooavito.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание комментария")
public class CommentRequest {

    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 1, max = 1000, message = "Комментарий должен содержать от 1 до 1000 символов")
    @Schema(description = "Текст комментария", example = "Отличное объявление! Свяжитесь со мной")
    private String content;
}
