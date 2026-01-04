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
@Table(name = "interview_question")
public class InterviewQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private InterviewRoom room;
    
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionContent;
    
    @Column(columnDefinition = "TEXT")
    private String answer;
    
    @Column(nullable = false)
    private Integer orderIndex = 0;
    
    @Column(nullable = false)
    private LocalDateTime askedAt = LocalDateTime.now();
    
    private LocalDateTime answeredAt;
    
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, ANSWERED
    
    @Column(length = 20)
    private String evaluation; // PASS, FAIL, null (not evaluated yet)
}

