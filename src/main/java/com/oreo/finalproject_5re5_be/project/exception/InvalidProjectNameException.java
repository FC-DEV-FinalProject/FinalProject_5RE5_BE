package com.oreo.finalproject_5re5_be.project.exception;

import static com.oreo.finalproject_5re5_be.global.exception.ErrorCode.PROJECT_INVALID_NAME;

import com.oreo.finalproject_5re5_be.global.exception.BusinessException;

public class InvalidProjectNameException extends BusinessException {
    public InvalidProjectNameException(String message) {
        super(message, PROJECT_INVALID_NAME);
    }
}
