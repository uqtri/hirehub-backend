package org.example.hirehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AuthHandlerException {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class PasswordMismatchException extends RuntimeException {

        public PasswordMismatchException(String message) {
            super(message);
        }
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UserNotFoundException extends RuntimeException {

        public UserNotFoundException(String message) {
            super(message);
        }
    }
}
