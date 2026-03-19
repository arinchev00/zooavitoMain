package com.example.zooavito.controller;

import com.example.zooavito.config.ApiResponseAnnotations;
import com.example.zooavito.request.UserRegistrationRequest;
import com.example.zooavito.response.UserRegistrationResponse;
import com.example.zooavito.service.User.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/v1/api/registration")
@RequiredArgsConstructor
@Tag(name = "Сервис регистрации", description = "API для регистрации новых пользователей")
public class RegisterUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    @ApiResponseAnnotations.BusinessErrorResponse
    @ApiResponseAnnotations.InternalServerErrorResponse
    public UserRegistrationResponse registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        return userService.registerUser(request);
    }
}