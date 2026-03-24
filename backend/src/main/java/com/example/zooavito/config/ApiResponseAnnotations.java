package com.example.zooavito.config;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import java.lang.annotation.*;

public class ApiResponseAnnotations {

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(name = "ValidError", value = "{\"error\": \"Неверные данные запроса\"}")
            )
    )
    public @interface BadRequestResponse {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "400",
            description = "Ошибка авторизации",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(name = "AuthError", value = "{\"error\": \"Неверный email или пароль\"}")
            )
    )
    public @interface AuthErrorResponse {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "400",
            description = "Бизнес-ошибка (email уже существует, пароли не совпадают)",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "EmailExists", value = "{\"email\": \"Пользователь с таким email уже существует\"}"),
                            @ExampleObject(name = "PasswordsMismatch", value = "{\"confirmPassword\": \"Пароли не совпадают\"}")
                    }
            )
    )
    public @interface BusinessErrorResponse {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    public @interface UnauthorizedResponse {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content)
    public @interface ForbiddenResponse {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "404", description = "Ресурс не найден", content = @Content)
    public @interface NotFoundResponse {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "415", description = "Неподдерживаемый тип медиа (используйте multipart/form-data)", content = @Content)
    public @interface UnsupportedMediaResponse {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    public @interface InternalServerErrorResponse {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @BadRequestResponse
    @NotFoundResponse
    @InternalServerErrorResponse
    public @interface CommonGetResponses {}

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @BadRequestResponse
    @UnauthorizedResponse
    @ForbiddenResponse
    @InternalServerErrorResponse
    public @interface CommonPostResponses {}
}
