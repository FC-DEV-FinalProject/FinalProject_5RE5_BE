package com.oreo.finalproject_5re5_be.vc.exception;

import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;

public class VcNotFoundProjectException extends VcNotFoundException {
    public VcNotFoundProjectException() {
        super(ErrorCode.VC_NOT_FOUND_PROJECT_ERROR.getMessage(), ErrorCode.VC_NOT_FOUND_PROJECT_ERROR);
    }

    public VcNotFoundProjectException(String message) {
        super(message, ErrorCode.VC_NOT_FOUND_PROJECT_ERROR);
    }
}
