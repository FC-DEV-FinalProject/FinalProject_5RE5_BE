package com.oreo.finalproject_5re5_be.vc.exception;

import com.oreo.finalproject_5re5_be.global.exception.BusinessException;
import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;

public class VcAccessDeniedException extends BusinessException {
    public VcAccessDeniedException() {
        super(ErrorCode.VC_ACCESS_DENIED.getMessage(), ErrorCode.VC_ACCESS_DENIED);
    }

    public VcAccessDeniedException(String message) {
        super(message, ErrorCode.VC_ACCESS_DENIED);
    }
}