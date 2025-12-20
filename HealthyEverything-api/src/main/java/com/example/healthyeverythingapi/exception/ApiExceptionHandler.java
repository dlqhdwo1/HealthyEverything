package com.example.healthyeverythingapi.exception;

import com.example.healthyeverythingapi.search.controller.SearchController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {


    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<?> handleDuplicateEmail(DuplicateEmailException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "code", "DUPLICATE_EMAIL",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "code", "INVALID_CREDENTIALS",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "success", false,
                        "message", "INVALID_REQUEST",
                        "errors", errors
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> RecentTrainerFailed(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "code", "FAIL",
                        "message", e.getMessage() == null ? "INVALID_REQUEST" : e.getMessage()
                ));
    }




    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAny(Exception e) {
        String msg = e.getMessage() == null ? "" : e.getMessage();

        if ("INVALID_REFRESH_TOKEN".equals(msg)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                            "success", false,
                            "message", "INVALID_REFRESH_TOKEN"
                    )
            );
        }



        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "success", false,
                        "message", "INTERNAL_ERROR"
                )
        );
    }


    @ExceptionHandler(SearchController.TrainerNotFoundException.class)
    public ResponseEntity<?> handleTrainerNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("success", false, "message", "TRAINER_NOT_FOUND")
        );
    }




}
