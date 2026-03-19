package com.example.zooavito.service.User;

import com.example.zooavito.Exception.BusinessErrorType;
import com.example.zooavito.Exception.BusinessException;
import com.example.zooavito.model.Role;
import com.example.zooavito.model.User;
import com.example.zooavito.repository.RoleRepository;
import com.example.zooavito.repository.UserRepository;
import com.example.zooavito.request.UpdateUserRequest;
import com.example.zooavito.request.UserRegistrationRequest;
import com.example.zooavito.response.UpdateUserResponse;
import com.example.zooavito.response.UserRegistrationResponse;
import com.example.zooavito.service.Recaptcha.RecaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RecaptchaService recaptchaService;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        log.info("=== РЕГИСТРАЦИЯ НОВОГО ПОЛЬЗОВАТЕЛЯ === email: {}", request.getEmail());

        String token = request.getRecaptchaToken();
        if (token == null || token.isEmpty()) {
            log.warn("reCAPTCHA token отсутствует для email: {}", request.getEmail());
            throw new BusinessException(BusinessErrorType.RECAPTCHA_MISSING);
        }

        boolean isValid = recaptchaService.verifyToken(token);
        if (!isValid) {
            log.warn("reCAPTCHA verification failed for email: {}", request.getEmail());
            throw new BusinessException(BusinessErrorType.RECAPTCHA_FAILED);
        }

        if (findByEmail(request.getEmail()) != null) {
            throw new BusinessException(BusinessErrorType.EMAIL_ALREADY_EXISTS);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(BusinessErrorType.PASSWORDS_DO_NOT_MATCH);
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setTelephoneNumber(request.getTelephoneNumber());
        user.setPassword(request.getPassword());

        save(user);

        return buildUserResponse(user);
    }

    @Override
    public UserRegistrationResponse buildUserResponse(User user) {
        Set<String> roleTitles = null;
        if (user.getRoles() != null) {
            roleTitles = user.getRoles().stream()
                    .map(Role::getTitle)
                    .collect(Collectors.toSet());
        }

        return UserRegistrationResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .telephoneNumber(user.getTelephoneNumber())
                .roles(roleTitles)
                .build();
    }

    @Override
    public UpdateUserResponse updateUser(String email, UpdateUserRequest request) {
        log.info("=== ОБНОВЛЕНИЕ ПРОФИЛЯ ПОЛЬЗОВАТЕЛЯ === email: {}", email);
        log.info("RAW request data:");
        log.info("  fullName = '{}'", request.getFullName());
        log.info("  telephoneNumber = '{}'", request.getTelephoneNumber());
        log.info("  newPassword = '{}'", request.getNewPassword());
        log.info("  confirmNewPassword = '{}'", request.getConfirmNewPassword());
        log.info("  currentPassword = '{}'", request.getCurrentPassword() != null ? "provided" : "null");

        log.info("Проверки:");
        log.info("  hasNewPassword = {}", request.getNewPassword() != null && !request.getNewPassword().isEmpty());
        log.info("  hasConfirmPassword = {}", request.getConfirmNewPassword() != null && !request.getConfirmNewPassword().isEmpty());

        User user = findByEmail(email);
        if (user == null) {
            log.warn("Пользователь не найден: {}", email);
            throw new BusinessException(BusinessErrorType.USER_NOT_FOUND);
        }

        if (!bCryptPasswordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Неверный текущий пароль для пользователя: {}", email);
            throw new BusinessException(BusinessErrorType.INVALID_CURRENT_PASSWORD);
        }

        boolean hasNewPassword = request.getNewPassword() != null && !request.getNewPassword().isEmpty();
        boolean hasConfirmPassword = request.getConfirmNewPassword() != null && !request.getConfirmNewPassword().isEmpty();

        log.info("Статус полей пароля: hasNewPassword={}, hasConfirmPassword={}", hasNewPassword, hasConfirmPassword);

        if (hasNewPassword || hasConfirmPassword) {
            log.info("Хотя бы одно поле пароля заполнено, выполняем валидацию");

            if (!hasNewPassword) {
                log.error("Указан confirmNewPassword, но не указан newPassword - выбрасываем исключение");
                throw new BusinessException(
                        BusinessErrorType.NEW_PASSWORD_REQUIRED,
                        "newPassword",
                        "Необходимо указать новый пароль"
                );
            }

            if (!hasConfirmPassword) {
                log.error("Указан newPassword, но не указан confirmNewPassword - выбрасываем исключение");
                throw new BusinessException(
                        BusinessErrorType.CONFIRM_PASSWORD_REQUIRED,
                        "confirmNewPassword",
                        "Необходимо подтверждение нового пароля"
                );
            }

            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                log.warn("Новый пароль и подтверждение не совпадают");
                throw new BusinessException(BusinessErrorType.PASSWORDS_DO_NOT_MATCH);
            }

            if (request.getNewPassword().length() < 6) {
                log.warn("Новый пароль слишком короткий: {}", request.getNewPassword().length());
                throw new BusinessException(
                        BusinessErrorType.NEW_PASSWORD_TOO_SHORT,
                        "newPassword",
                        "Новый пароль должен содержать минимум 6 символов"
                );
            }

            log.info("Все проверки пароля пройдены, обновляем пароль для пользователя: {}", email);
            user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        } else {
            log.info("Пароль не изменяется (поля newPassword и confirmNewPassword пустые)");
        }

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            log.info("Обновление имени: {} -> {}", user.getFullName(), request.getFullName());
            user.setFullName(request.getFullName());
        } else {
            log.info("Имя не изменяется");
        }

        if (request.getTelephoneNumber() != null && !request.getTelephoneNumber().isEmpty()) {
            log.info("Обновление телефона: {} -> {}", user.getTelephoneNumber(), request.getTelephoneNumber());
            user.setTelephoneNumber(request.getTelephoneNumber());
        } else {
            log.info("Телефон не изменяется");
        }

        userRepository.save(user);
        log.info("Профиль пользователя успешно обновлен в БД");

        return UpdateUserResponse.from(user);
    }
}