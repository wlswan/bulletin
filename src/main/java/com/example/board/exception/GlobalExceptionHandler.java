package com.example.board.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    //json으로 프론트엔드랑 따로 분리되어서 할때
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFound(PostNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFound(CommentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    //뷰 렌더링 타임리프로
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailAlreadyExists(EmailAlreadyExistsException e, Model model) {

        model.addAttribute("errorMessage", e.getMessage());
        return "register";
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
