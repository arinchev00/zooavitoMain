package com.example.zooavito.controller;

import com.example.zooavito.response.UserAdminResponse;
import com.example.zooavito.service.Admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/admin")
@RequiredArgsConstructor
@Tag(name = "Администрирование", description = "API для администраторов")
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "Получить список всех пользователей")
    public List<UserAdminResponse> getAllUsers() {
        log.info("Запрос списка всех пользователей");
        return adminService.getAllUsers();
    }

    @PutMapping("/users/{userId}/block")
    @Operation(summary = "Заблокировать пользователя")
    public void blockUser(@PathVariable Long userId) {
        log.info("Блокировка пользователя с ID: {}", userId);
        adminService.blockUser(userId);
    }

    @PutMapping("/users/{userId}/unblock")
    @Operation(summary = "Разблокировать пользователя")
    public void unblockUser(@PathVariable Long userId) {
        log.info("Разблокировка пользователя с ID: {}", userId);
        adminService.unblockUser(userId);
    }

    @PutMapping("/users/{userId}/role")
    @Operation(summary = "Изменить роль пользователя")
    public void changeUserRole(@PathVariable Long userId, @RequestParam String role) {
        log.info("Изменение роли пользователя {} на {}", userId, role);
        adminService.changeUserRole(userId, role);
    }
}