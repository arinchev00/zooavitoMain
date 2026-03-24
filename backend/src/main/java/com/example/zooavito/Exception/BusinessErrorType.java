package com.example.zooavito.Exception;

public enum BusinessErrorType {
    // Регистрация
    EMAIL_ALREADY_EXISTS("email", "Пользователь с таким email уже существует"),
    PASSWORDS_DO_NOT_MATCH("confirmPassword", "Пароли не совпадают"),

    // Авторизация
    ACCOUNT_BLOCKED("account", "Ваш аккаунт заблокирован. Обратитесь к администратору."),
    RECAPTCHA_MISSING("recaptchaToken", "reCAPTCHA токен не получен"),
    RECAPTCHA_FAILED("recaptchaToken", "Капча не пройдена"),

    // Профиль пользователя
    USER_NOT_FOUND("email", "Пользователь не найден"),
    INVALID_CURRENT_PASSWORD("currentPassword", "Неверный текущий пароль"),
    NEW_PASSWORD_REQUIRED("newPassword", "Необходимо указать новый пароль"),
    CONFIRM_PASSWORD_REQUIRED("confirmNewPassword", "Необходимо подтверждение нового пароля"),
    NEW_PASSWORD_TOO_SHORT("newPassword", "Новый пароль должен содержать минимум 6 символов"),

    // Админка
    ROLE_NOT_FOUND("role", "Роль не найдена");

    private final String field;
    private final String defaultMessage;

    BusinessErrorType(String field, String defaultMessage) {
        this.field = field;
        this.defaultMessage = defaultMessage;
    }

    public String getField() {
        return field;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}