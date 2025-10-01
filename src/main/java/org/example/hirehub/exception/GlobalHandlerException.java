package org.example.hirehub.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, ?>> handleGlobalError(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(JobHandlerException.JobNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleJobExceptions(JobHandlerException.JobNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResumeHandlerException.ResumeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResumeExceptions(ResumeHandlerException.ResumeNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SkillHandlerException.SkillNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSkillExceptions(SkillHandlerException.SkillNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserHandlerException.RecruiterNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRecruiterExceptions(UserHandlerException.RecruiterNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserHandlerException.UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserExceptions(UserHandlerException.UserNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}