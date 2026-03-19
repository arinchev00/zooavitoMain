package com.example.zooavito.service.Admin;

import com.example.zooavito.Exception.BusinessErrorType;
import com.example.zooavito.Exception.BusinessException;
import com.example.zooavito.model.Role;
import com.example.zooavito.model.User;
import com.example.zooavito.repository.RoleRepository;
import com.example.zooavito.repository.UserRepository;
import com.example.zooavito.response.UserAdminResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserAdminResponse> getAllUsers() {
        log.info("Получение списка всех пользователей");

        return userRepository.findAll().stream()
                .map(this::convertToAdminResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void blockUser(Long userId) {
        log.info("Блокировка пользователя ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(BusinessErrorType.USER_NOT_FOUND));

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unblockUser(Long userId) {
        log.info("Разблокировка пользователя ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(BusinessErrorType.USER_NOT_FOUND));

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changeUserRole(Long userId, String role) {
        log.info("Изменение роли пользователя {} на {}", userId, role);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(BusinessErrorType.USER_NOT_FOUND));

        Role newRole = roleRepository.findByTitle(role)
                .orElseThrow(() -> new BusinessException(BusinessErrorType.ROLE_NOT_FOUND));

        user.getRoles().clear();
        user.getRoles().add(newRole);
        userRepository.save(user);
    }

    private UserAdminResponse convertToAdminResponse(User user) {
        return UserAdminResponse.from(user);
    }
}