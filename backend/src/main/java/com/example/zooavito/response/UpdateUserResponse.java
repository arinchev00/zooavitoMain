package com.example.zooavito.response;

import com.example.zooavito.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserResponse {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Email пользователя", example = "test@test.com")
    private String email;

    @Schema(description = "Полное имя пользователя", example = "Иванов Иван")
    private String fullName;

    @Schema(description = "Номер телефона", example = "+7(990)800-70-60")
    private String telephoneNumber;

    @Schema(description = "Сообщение о результате операции", example = "Профиль успешно обновлен")
    private String message;

    public static UpdateUserResponse from(User user) {
        return UpdateUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .telephoneNumber(user.getTelephoneNumber())
                .message("Профиль успешно обновлен")
                .build();
    }
}