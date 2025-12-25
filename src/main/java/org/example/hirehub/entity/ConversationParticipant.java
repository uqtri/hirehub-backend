package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "conversation_participant")
public class ConversationParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime joinedAt = LocalDateTime.now();
    private LocalDateTime lastReadAt;
    private boolean isDeleted = false;

    // Trưởng nhóm (leader) của group conversation
    private boolean isLeader = false;

    // Thời điểm người dùng rời khỏi group (có thể được mời vào lại)
    // Khi rời thì set leavedAt = now, khi được mời lại thì set leavedAt = null
    private LocalDateTime leavedAt;

    // Thời điểm tin nhắn bắt đầu được hiển thị cho user này
    // Khi mới được mời vào hoặc được mời lại thì set deletedAt = now
    // User chỉ thấy được tin nhắn sau thời điểm này
    private LocalDateTime deletedAt;

    public ConversationParticipant(Conversation conversation, User user) {
        this.conversation = conversation;
        this.user = user;
        this.deletedAt = LocalDateTime.now(); // Khi mới tham gia, chỉ thấy tin nhắn từ thời điểm này
    }
}

