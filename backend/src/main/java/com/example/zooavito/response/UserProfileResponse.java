package com.example.zooavito.response;

import com.example.zooavito.model.Role;
import com.example.zooavito.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class UserProfileResponse {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Email пользователя", example = "test@test.com")
    private String email;

    @Schema(description = "Полное имя пользователя", example = "Иванов Иван")
    private String fullName;

    @Schema(description = "Номер телефона", example = "+7(990)800-70-60")
    private String telephoneNumber;

    @Schema(description = "Роли пользователя", example = "[\"ROLE_USER\"]")
    private Set<String> roles;

    public static UserProfileResponse from(User user) {
        Set<String> roleTitles = user.getRoles().stream()
                .map(Role::getTitle)
                .collect(Collectors.toSet());

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .telephoneNumber(user.getTelephoneNumber())
                .roles(roleTitles)
                .build();
    }
}