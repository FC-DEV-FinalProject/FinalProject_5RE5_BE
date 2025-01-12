package com.oreo.finalproject_5re5_be.project.exception;

import com.oreo.finalproject_5re5_be.global.exception.BusinessException;
import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;

public class InvalidProjectNameException extends BusinessException {
    public InvalidProjectNameException(String message) {
        super(message, ErrorCode.PROJECT_INVALID_NAME);
    }
}
