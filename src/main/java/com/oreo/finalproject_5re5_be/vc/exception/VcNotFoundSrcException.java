package com.oreo.finalproject_5re5_be.vc.exception;

import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;

public class VcNotFoundSrcException extends VcNotFoundException {
    public VcNotFoundSrcException() {
        super(ErrorCode.VC_NOT_FOUND_SRC_ERROR.getMessage(), ErrorCode.VC_NOT_FOUND_SRC_ERROR);
    }

    public VcNotFoundSrcException(String message) {
        super(message, ErrorCode.VC_NOT_FOUND_SRC_ERROR);
    }
}
