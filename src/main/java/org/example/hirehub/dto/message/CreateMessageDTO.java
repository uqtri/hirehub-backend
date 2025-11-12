package org.example.hirehub.dto.message;

import org.example.hirehub.dto.user.UserSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class CreateMessageDTO {
    private String message;

    private String senderEmail;
    private String receiverEmail;
}
