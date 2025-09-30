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
public class Resume {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String link;
    @ManyToOne
    private User user;
    @ManyToOne
    private Job job;
    private String status = "NOT VIEW";
    @Column(columnDefinition = "TEXT")
    private String cover_letter;
    private boolean isDeleted = false;
    private LocalDateTime createdAt = LocalDateTime.now();

}
