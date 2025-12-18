package org.example.hirehub.dto.conversation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.hirehub.dto.user.UserSummaryDTO;

import java.util.List;

/**
 * DTO để gửi thông báo socket khi có sự kiện trong group
 * (kick, leave, invite)
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GroupEventDTO {

    public enum EventType {
        MEMBER_KICKED, // Thành viên bị kick
        MEMBER_LEFT, // Thành viên tự rời
        MEMBER_INVITED, // Thành viên mới được mời vào
        GROUP_CREATED, // Nhóm mới được tạo
        GROUP_DISBANDED // Nhóm bị giải tán
    }

    private Long conversationId;
    private EventType eventType;

    // Người thực hiện hành động (leader kick, hoặc người rời)
    private UserSummaryDTO actor;

    // Những người bị ảnh hưởng (người bị kick, hoặc người được mời)
    private List<UserSummaryDTO> affectedUsers;

    // Tin nhắn hệ thống tương ứng
    private String systemMessage;
}
