package org.example.hirehub.dto.chatbot;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequestDTO {
    private String message;
    private List<MessageDTO> history;
    private String messageType;
}
