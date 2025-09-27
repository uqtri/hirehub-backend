package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.key.JobSkillKey;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobSkill {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private JobSkillKey jobSkillKey;

    @ManyToOne
    @MapsId("jobId")
    private Job job;
    @ManyToOne
    @MapsId("skillId")
    private Skill skill;

}
