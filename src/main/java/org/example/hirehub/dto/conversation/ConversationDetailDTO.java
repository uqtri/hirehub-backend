package org.example.hirehub.dto.conversation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.message.MessageDetailDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConversationDetailDTO {
    private Long id;
    private String type;
    private String name;
    // Id trưởng nhóm (leader) nếu là GROUP, null nếu DIRECT
    private Long leaderId;
    private List<UserSummaryDTO> participants;
    private MessageDetailDTO lastMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long unreadCount;
}

