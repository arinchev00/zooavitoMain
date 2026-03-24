package com.example.zooavito.Exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessErrorType errorType;
    private final String field;
    private final String message;

    // Конструктор с типом ошибки (использует сообщение по умолчанию)
    public BusinessException(BusinessErrorType errorType) {
        super(errorType.getDefaultMessage());
        this.errorType = errorType;
        this.field = errorType.getField();
        this.message = errorType.getDefaultMessage();
    }

    // Конструктор с типом ошибки, полем и сообщением (для динамических полей)
    public BusinessException(BusinessErrorType errorType, String field, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
        this.field = field;
        this.message = customMessage;
    }
}
