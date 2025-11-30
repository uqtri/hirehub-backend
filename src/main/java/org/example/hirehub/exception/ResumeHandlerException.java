package org.example.hirehub.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

public class ResumeHandlerException {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResumeNotFoundException extends RuntimeException {
        public ResumeNotFoundException(Long id) {
            super("Không tìm thấy resume với id: " + id);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class ResumeAlreadyApplied extends RuntimeException {
        public ResumeAlreadyApplied(Long id) {
            super("Đã nộp công việc với id:" + id);
        }
    }

}
