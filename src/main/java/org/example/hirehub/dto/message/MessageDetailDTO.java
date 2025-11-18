package org.example.hirehub.dto.message;

import org.example.hirehub.dto.user.UserDetailDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class MessageDetailDTO {
    private Long id;
    private String message;
    private LocalDateTime createdAt;

    private UserSummaryDTO sender;
    private UserSummaryDTO receiver;

    private List<UserDetailDTO> seenUsers;
}
