package org.example.hirehub.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
public class Skill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "skill")
    private List<ExperienceSkill> skills;

    @OneToMany(mappedBy = "skill")
    private List<UserSkill> userSkills;

    private boolean isDeleted = false;


}

