package com.oreo.finalproject_5re5_be.project.exception;

import com.oreo.finalproject_5re5_be.global.exception.BusinessException;
import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;

public class ProjectNotMemberException extends BusinessException {
    public ProjectNotMemberException() {
        super(ErrorCode.PROJECT_ACCESS_DENIED.getMessage(), ErrorCode.PROJECT_ACCESS_DENIED);
    }

    public ProjectNotMemberException(String message) {
        super(message, ErrorCode.PROJECT_ACCESS_DENIED);
    }
}
