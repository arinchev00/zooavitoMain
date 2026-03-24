package com.example.zooavito.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationRequest {

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 100, message = "Имя должно содержать от 2 до 100 символов")
    @Schema(description = "Полное имя пользователя", example = "Иванов Иван")
    private String fullName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Schema(description = "Электронная почта", example = "test@test.com")
    private String email;

    @Pattern(
            regexp = "^$|^\\+\\d\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}$",
            message = "Телефон должен быть в формате: +x(xxx)xxx-xx-xx (например, +7(990)800-70-60) или пустым"
    )
    @Schema(description = "Номер телефона (необязательно)", example = "+7(990)800-70-60")
    private String telephoneNumber;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @Schema(description = "Пароль", example = "12345678")
    private String password;

    @NotBlank(message = "Подтверждение пароля обязательно")
    @Schema(description = "Повтор пароля", example = "12345678")
    private String confirmPassword;

    @Schema(description = "Токен Google reCAPTCHA для проверки, что запрос от человека", example = "any_string_works")
    private String recaptchaToken;
}