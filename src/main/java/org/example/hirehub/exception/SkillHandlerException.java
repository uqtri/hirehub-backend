package org.example.hirehub.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

public class SkillHandlerException {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class SkillNotFoundException extends RuntimeException {
        public SkillNotFoundException(Long id) {
            super("Không tìm thấy kĩ năng với id: " + id);
        }
    }

}
