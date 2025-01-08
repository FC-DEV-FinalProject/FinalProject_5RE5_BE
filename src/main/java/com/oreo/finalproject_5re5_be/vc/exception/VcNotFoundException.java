package com.oreo.finalproject_5re5_be.vc.exception;

import com.oreo.finalproject_5re5_be.global.exception.EntityNotFoundException;
import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;

public class VcNotFoundException extends EntityNotFoundException {

  public VcNotFoundException(String message) {
    super(message, ErrorCode.VC_NOT_FOUND_ERROR);
  }

  public VcNotFoundException(String message, ErrorCode errorCode) {
    super(message, errorCode);
  }
}
