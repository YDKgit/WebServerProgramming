package com.example.springstudy.exception;

import com.example.springstudy.dto.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<CommonResponse<String>> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(new CommonResponse<>(false, exception.getMessage()));
    }
}
