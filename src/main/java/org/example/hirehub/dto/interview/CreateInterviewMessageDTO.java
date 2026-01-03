package org.example.hirehub.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInterviewMessageDTO {
    private String roomCode;
    private Long senderId;
    private String senderRole;
    private String type;
    private String content;
}

