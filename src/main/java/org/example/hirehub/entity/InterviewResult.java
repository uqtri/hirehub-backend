package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "interview_result")
public class InterviewResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "room_id", nullable = false, unique = true)
    private InterviewRoom room;
    
    @Column(nullable = false)
    private Integer score; // 1-10
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(columnDefinition = "TEXT")
    private String privateNotes;
    
    @Column(nullable = false, length = 20)
    private String recommendation; // PASS, FAIL
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

