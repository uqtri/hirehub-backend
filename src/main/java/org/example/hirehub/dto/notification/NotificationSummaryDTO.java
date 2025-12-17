package org.example.hirehub.dto.notification;

import jakarta.persistence.Column;
import lombok.*;
import org.example.hirehub.dto.user.UserSummaryDTO;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@Builder

public class NotificationSummaryDTO {
    private Long id;
    private String type;
    private UserSummaryDTO user;
    private String title;
    private String content;

    private String redirectUrl;

    private boolean isRead = false;

    private LocalDateTime createdAt;

    private Boolean isDeleted;
}
