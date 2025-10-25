package org.example.hirehub.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
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

}
