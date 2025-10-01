package org.example.hirehub.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

public class JobHandlerException {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class JobNotFoundException extends RuntimeException {
        public JobNotFoundException(Long id) {
            super("Không tìm thấy bài đăng tuyển dụng với: " + id);
        }
    }

}
