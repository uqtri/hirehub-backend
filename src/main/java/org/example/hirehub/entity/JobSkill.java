package org.example.hirehub.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.example.hirehub.key.JobSkillKey;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter

public class JobSkill {

    @EmbeddedId
    private JobSkillKey jobSkillKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jobId") // map tới JobSkillKey.jobId
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId") // map tới JobSkillKey.skillId
    @JoinColumn(name = "skill_id")
    private Skill skill;
    private LocalDateTime createdAt = LocalDateTime.now();

    public JobSkill(Job job, Skill skill) {
        this.job = job;
        this.skill = skill;
        this.jobSkillKey = new JobSkillKey(job.getId(), skill.getId());
    }
}

