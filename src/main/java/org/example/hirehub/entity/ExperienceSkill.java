package org.example.hirehub.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.MapsId;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.example.hirehub.key.ExperienceSkillKey;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter

public class ExperienceSkill {
    @EmbeddedId
    private ExperienceSkillKey experienceSkillKey;

    @ManyToOne
    @MapsId("experienceId")
    private Experience experience;
    @ManyToOne
    @MapsId("skillId")
    private Skill skill;
    private LocalDateTime createdAt = LocalDateTime.now();

}
