package com.oreo.finalproject_5re5_be.vc.exception;

import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;

public class VcNotFoundTrgException extends VcNotFoundException {
    public VcNotFoundTrgException() {
        super(ErrorCode.VC_NOT_FOUND_TRG_ERROR.getMessage(), ErrorCode.VC_NOT_FOUND_TRG_ERROR);
    }

    public VcNotFoundTrgException(String message) {
        super(message, ErrorCode.VC_NOT_FOUND_TRG_ERROR);
    }
}
