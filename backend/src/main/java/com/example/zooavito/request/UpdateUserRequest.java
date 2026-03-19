package com.example.zooavito.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(min = 2, max = 100, message = "Имя должно содержать от 2 до 100 символов")
    @Schema(description = "Полное имя пользователя", example = "Иванов Иван")
    private String fullName;

    @Pattern(
            regexp = "^\\+\\d\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}$",
            message = "Телефон должен быть в формате: +x(xxx)xxx-xx-xx (например, +7(990)800-70-60)"
    )
    @Schema(description = "Номер телефона", example = "+7(990)800-70-60")
    private String telephoneNumber;

    @Size(min = 6, message = "Новый пароль должен содержать минимум 6 символов")
    @Schema(description = "Новый пароль (если нужно изменить)", example = "newpassword123")
    private String newPassword;

    @Schema(description = "Подтверждение нового пароля", example = "newpassword123")
    private String confirmNewPassword;

    @NotBlank(message = "Текущий пароль обязателен для подтверждения изменений")
    @Schema(description = "Текущий пароль для подтверждения личности", example = "oldpassword123")
    private String currentPassword;
}