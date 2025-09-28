package org.example.hirehub.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.key.UserSkillKey;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSkill {
    @EmbeddedId
    private UserSkillKey userSkillKey;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("skillId")
    private Skill skill;

    public UserSkill(User user, Skill skill) {
        this.user  = user;
        this.skill = skill;
        this.userSkillKey = new UserSkillKey(user.getId(), skill.getId());
    }
}
