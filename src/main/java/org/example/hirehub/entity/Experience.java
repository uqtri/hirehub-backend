package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Experience {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @ManyToOne
  private User user;

  @ManyToOne
  private User company;

  private String position;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String description;
  private String image;

  private boolean isDeleted = false;

  @OneToMany(mappedBy = "experience")
  private List<ExperienceSkill> skills;
}
