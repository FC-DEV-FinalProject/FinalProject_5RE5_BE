package com.oreo.finalproject_5re5_be.vc.exception;

import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;

public class VcNotFoundTextException extends VcNotFoundException {
    public VcNotFoundTextException() {
        super(ErrorCode.VC_NOT_FOUND_TEXT_ERROR.getMessage(), ErrorCode.VC_NOT_FOUND_TEXT_ERROR);
    }

    public VcNotFoundTextException(String message) {
        super(message, ErrorCode.VC_NOT_FOUND_TEXT_ERROR);
    }
}