package org.example.hirehub.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

public class UserHandlerException {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class RecruiterNotFoundException extends RuntimeException {
        public RecruiterNotFoundException(Long id) {
            super("Không tìm thấy nhà đăng tuyển dụng với id: " + id);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(Long id) {
            super("Không tìm thấy ứng viên với: " + id);
        }
    }
}
