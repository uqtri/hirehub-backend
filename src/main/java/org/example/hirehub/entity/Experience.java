package org.example.hirehub.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter

public class Experience {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @ManyToOne
  private User user;

  @ManyToOne
  private User company;

  private String position;
  private LocalDate startDate;
  private LocalDate endDate;
  private String description;
  private String image;
  private String type;

  private boolean isDeleted = false;

  @OneToMany(mappedBy = "experience")
  private List<ExperienceSkill> skills;
  private LocalDateTime createdAt = LocalDateTime.now();

}
