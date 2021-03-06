package com.cuzoffservice.infrastructure.config;

import com.cuzoffservice.interfaces.dto.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleResourceNotFoundException(Exception ex, WebRequest request) {
        return createMessagesResponseEntity("500", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(f -> {
            fieldErrors.add(String.format("%s: %s", f.getField(), f.getDefaultMessage()));
        });
        return new ResponseEntity<>(ErrorResponseDto.builder()
                .errors(fieldErrors)
                .message(ex.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(UserServiceException ex, WebRequest request) {
        return createMessagesResponseEntity(String.valueOf(ex.getCode()), ex.getMessage(), HttpStatus.valueOf(ex.getCode()));
    }

    private ResponseEntity<Object> createMessagesResponseEntity(String code, String message, HttpStatus status) {
        return new ResponseEntity<Object>(ErrorResponseDto.builder().code(code).message(message).build(), status);
    }
}
