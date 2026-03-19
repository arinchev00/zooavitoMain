package com.example.zooavito.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Email обязателен")
    @Schema(description = "Электронная почта", example = "admin123@admin.com")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Schema(description = "Пароль", example = "admin123")
    private String password;

    @Schema(description = "Токен Google reCAPTCHA для проверки, что запрос от человека")
    private String recaptchaToken;
}
