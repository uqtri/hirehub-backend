package org.example.hirehub.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.key.ExperienceSkillKey;
import org.example.hirehub.key.JobSkillKey;
import org.example.hirehub.key.UserMessageKey;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter

public class UserMessage {
    @EmbeddedId
    private UserMessageKey userMessageKey;

    @ManyToOne
    @MapsId("userId")
    private User user;
    @ManyToOne
    @MapsId("messageId")
    private Message message;
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserMessage(User user, Message message) {
        this.user = user;
        this.message = message;
        this.userMessageKey = new UserMessageKey(user.getId(), message.getId());
    }
}
