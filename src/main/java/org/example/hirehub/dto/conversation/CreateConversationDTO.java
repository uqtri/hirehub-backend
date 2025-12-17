package org.example.hirehub.dto.conversation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateConversationDTO {
    @NotNull
    private String type; // "DIRECT" or "GROUP"
    
    private String name; // For group conversations

    // Id của người tạo conversation (sẽ làm trưởng nhóm nếu là GROUP)
    @NotNull
    private Long creatorId;
    
    @NotNull
    private List<Long> participantIds; // At least 2 users for DIRECT, can be more for GROUP
}

