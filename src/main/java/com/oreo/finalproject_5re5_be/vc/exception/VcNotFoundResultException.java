package com.oreo.finalproject_5re5_be.vc.exception;

import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;

public class VcNotFoundResultException extends VcNotFoundException{

  public VcNotFoundResultException() {
    super(ErrorCode.VC_NOT_FOUND_RESULT_ERROR.getMessage(), ErrorCode.VC_NOT_FOUND_RESULT_ERROR);
  }

  public VcNotFoundResultException(String message) {
    super(message, ErrorCode.VC_NOT_FOUND_RESULT_ERROR);
  }
}
