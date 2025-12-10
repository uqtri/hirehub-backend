package org.example.hirehub.dto.message;

import jakarta.validation.constraints.NotNull;
import org.example.hirehub.dto.user.UserSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class CreateMessageDTO {
    private String content;
    private String fileName;

    private String type;

    private String senderEmail;
    private String receiverEmail;
}
