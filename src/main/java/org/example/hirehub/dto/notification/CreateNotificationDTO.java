package org.example.hirehub.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.enums.NotificationType;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class CreateNotificationDTO {
    private Long userId;
    private NotificationType type;
    private String title;
    private String content;
    private String redirectUrl;
}
