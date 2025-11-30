package org.example.hirehub.dto.chatbot;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChatbotRequestDTO {
    String message;
    String type;
    MultipartFile resume;
    String resumeId;
}
