package com.example.zooavito.controller;

import com.example.zooavito.config.ApiResponseAnnotations;
import com.example.zooavito.request.AuthRequest;
import com.example.zooavito.response.AuthResponse;
import com.example.zooavito.service.Auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для входа и управления профилем")
public class AuthController {

    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Вход в систему", description = "Аутентификация пользователя и получение JWT токена")
    @ApiResponse(responseCode = "200", description = "Успешная аутентификация")
    @ApiResponseAnnotations.AuthErrorResponse
    @ApiResponseAnnotations.InternalServerErrorResponse
    public ResponseEntity<AuthResponse> auth(@RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.authenticateUser(authRequest);
        return ResponseEntity.ok(response);
    }
}
