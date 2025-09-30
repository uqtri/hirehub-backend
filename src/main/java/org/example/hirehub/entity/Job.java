package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String apply_link;
    @ManyToOne
    private User recruiter;
    private String level;
    private boolean is_banned;
    private String workspace;
    @OneToMany(mappedBy = "job")
    private List<JobSkill> skills;
    private boolean isDeleted = false;
    private Long hit_counter = 0L;
    private LocalDateTime postingDate = LocalDateTime.now();

}
