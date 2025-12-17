package org.example.hirehub.dto.notification;

import lombok.*;
import org.example.hirehub.enums.NotificationType;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@Builder

public class CreateNotificationDTO {
    private Long userId;
    private String type;
    private String title;
    private String content;
    private String redirectUrl;
}
