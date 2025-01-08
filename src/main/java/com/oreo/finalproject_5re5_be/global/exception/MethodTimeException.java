package com.oreo.finalproject_5re5_be.global.exception;

public class MethodTimeException extends BusinessException {
    public MethodTimeException(String message) {
        super(message, ErrorCode.METHODTIME_ERROR);
    }
}
