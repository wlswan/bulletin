package com.example.board.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFound(PostNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    //ErrorResponse 객체를 만들어 문자열 대신 JSON 객체로 만들어서 하는 게 더 좋음
//    public class ErrorResponse {
//        private String message;
//        private int status;
//    }
//    @ExceptionHandler(PostNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handlePostNotFound(PostNotFoundException e) {
//        ErrorResponse error = new ErrorResponse(e.getMessage(), 404);
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//    }

}
