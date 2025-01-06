package com.oreo.finalproject_5re5_be.project.exception;

import com.oreo.finalproject_5re5_be.global.dto.response.ErrorResponseDto;
import com.oreo.finalproject_5re5_be.global.dto.response.ResponseDto;
import com.oreo.finalproject_5re5_be.global.exception.BusinessException;
import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(value = "com.oreo.finalproject_5re5_be.project")
public class ProjectExceptionHandler {

    @ExceptionHandler(value = ProjectNotFoundException.class) // 어떤 예외클래스를 처리할건지 지정
    public ResponseEntity<ResponseDto<String>> ProjectNotFoundExceptionHandler(ProjectNotFoundException e, HttpServletRequest request) {
        log.error(
                "[Project] ProjectNotFoundExceptionHandler 호출 , {} , {}",
                request.getRequestURI(),
                e.getMessage());
        return ResponseEntity.status(ErrorCode.PROJECT_NOT_FOUND_ERROR.getStatus())
                .body(new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(value = ProjectNotMemberException.class) // 어떤 예외클래스를 처리할건지 지정
    public ResponseEntity<ResponseDto<String>> ProjectNotMemberExceptionHandler(ProjectNotMemberException e, HttpServletRequest request) {
        log.error(
            "[Project] ProjectNotMemberExceptionHandler 호출 , {} , {}",
            request.getRequestURI(),
            e.getMessage());
        return ResponseEntity.status(ErrorCode.PROJECT_ACCESS_DENIED.getStatus())
            .body(new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> runtimeExceptionHandler(RuntimeException e) {
        log.error("[project] RuntimeException 호출 : {}", e);

        ErrorResponseDto errorResponseDto =
            ErrorResponseDto.of(
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(errorResponseDto);
    }

    // BusinessException 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> ttsExceptionHandler(BusinessException e) {
        log.error("[project] BusinessException 호출 : {} ", e);

        ErrorResponseDto errorResponseDto =
            ErrorResponseDto.of(e.getErrorCode().getStatus(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(errorResponseDto);
    }
}
