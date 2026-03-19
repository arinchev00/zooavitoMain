package com.example.zooavito.controller;

import com.example.zooavito.config.ApiResponseAnnotations;
import com.example.zooavito.model.User;
import com.example.zooavito.request.UpdateUserRequest;
import com.example.zooavito.response.UpdateUserResponse;
import com.example.zooavito.response.UserProfileResponse;
import com.example.zooavito.service.User.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/user")
@RequiredArgsConstructor
@Tag(name = "Управление пользователем", description = "API для управления профилем пользователя")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Получить информацию о текущем пользователе")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponseAnnotations.CommonGetResponses
    public ResponseEntity<UserProfileResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        log.info("Запрос информации о пользователе: {}", email);

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(UserProfileResponse.from(user));
    }

    @PutMapping("/me")
    @ResponseStatus(org.springframework.http.HttpStatus.OK)
    @Operation(summary = "Обновить профиль пользователя")
    @ApiResponse(responseCode = "200", description = "Профиль успешно обновлен")
    @ApiResponseAnnotations.BusinessErrorResponse
    public UpdateUserResponse updateUser(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        log.info("Обновление профиля пользователя: {}", email);

        return userService.updateUser(email, request);
    }
}
