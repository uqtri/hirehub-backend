package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private University university;

    @Column(columnDefinition = "TEXT")
    private String logo;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String major;
    private String degree;
    @ManyToOne
    private User user;
    private LocalDateTime createdAt = LocalDateTime.now();
}
