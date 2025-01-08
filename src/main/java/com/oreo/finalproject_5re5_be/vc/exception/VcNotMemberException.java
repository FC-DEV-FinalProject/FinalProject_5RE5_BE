package com.oreo.finalproject_5re5_be.vc.exception;

import com.oreo.finalproject_5re5_be.global.exception.BusinessException;
import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;

public class VcNotMemberException extends VcNotFoundException {
    public VcNotMemberException() {
        super(ErrorCode.VC_NOT_FOUND_MEMBER_ERROR.getMessage(), ErrorCode.VC_NOT_FOUND_MEMBER_ERROR);
    }

    public VcNotMemberException(String message) {
        super(message, ErrorCode.VC_NOT_FOUND_MEMBER_ERROR);
    }
}
