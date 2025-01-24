package com.oreo.finalproject_5re5_be.vc.exception;

import com.oreo.finalproject_5re5_be.global.dto.response.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(value = "com.oreo.finalproject_5re5_be.vc")
public class VcExceptionHandler {

    @ExceptionHandler(value = VcNotFoundProjectException.class)
    public ResponseEntity<ResponseDto<String>> handleVcNotFoundProjectException(
            VcNotFoundProjectException e, HttpServletRequest request) {
        log.error("VC Not Found Project Exception: {}, {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(value = VcNotFoundSrcException.class)
    public ResponseEntity<ResponseDto<String>> handleVcNotFoundSrcException(
            VcNotFoundSrcException e, HttpServletRequest request) {
        log.error("VC Not Found Src Exception: {}, {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(value = VcNotFoundTrgException.class)
    public ResponseEntity<ResponseDto<String>> handleVcNotFoundTrgException(
            VcNotFoundTrgException e, HttpServletRequest request) {
        log.error("VC Not Found Trg Exception: {}, {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(value = VcNotFoundResultException.class)
    public ResponseEntity<ResponseDto<String>> handleVcNotFoundResultException(
            VcNotFoundResultException e, HttpServletRequest request) {
        log.error("VC Not Found Result Exception: {}, {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(value = VcNotFoundTextException.class)
    public ResponseEntity<ResponseDto<String>> handleVcNotFoundTextException(
            VcNotFoundTextException e, HttpServletRequest request) {
        log.error("VC Not Found Text Exception: {}, {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(value = VcNotMemberException.class)
    public ResponseEntity<ResponseDto<String>> handleVcNotMemberException(
            VcNotMemberException e, HttpServletRequest request) {
        log.error("VC Not Member Exception: {}, {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ResponseDto<String>> handleRuntimeException(
            RuntimeException e, HttpServletRequest request) {
        log.error("RuntimeException: {}, {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다."));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseDto<String>> handleException(
            Exception e, HttpServletRequest request) {
        log.error(
                "VC RestController 내 handlerException 호출, {}, {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}
