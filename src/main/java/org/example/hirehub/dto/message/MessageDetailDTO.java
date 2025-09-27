package org.example.hirehub.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.user.UserSummaryDTO;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDetailDTO {
    private Long id;
    private String message;
    private LocalDateTime createdTime;

    private UserSummaryDTO sender;
    private UserSummaryDTO receiver;
}
