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

    public ConversationParticipant(Conversation conversation, User user) {
        this.conversation = conversation;
        this.user = user;
    }
}

