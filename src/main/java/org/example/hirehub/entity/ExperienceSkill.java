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

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceSkill {
    @EmbeddedId
    private ExperienceSkillKey experienceSkillKey;

    @ManyToOne
    @MapsId("experienceId")
    private Experience experience;
    @ManyToOne
    @MapsId("skillId")
    private Skill skill;
}
