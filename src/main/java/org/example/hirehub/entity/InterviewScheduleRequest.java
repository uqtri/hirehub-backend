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
@Table(name = "interview_schedule_request")
public class InterviewScheduleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, SELECTED, EXPIRED, CANCELLED

    @Column(nullable = false, length = 20)
    private String interviewType = "CHAT"; // CHAT, VIDEO

    @Column(nullable = false, length = 20)
    private String interviewMode = "LIVE"; // LIVE, ASYNC

    @Column(nullable = false)
    private Integer roundNumber = 1;

    private Long previousRoomId;

    private Long selectedTimeSlotId; // The slot chosen by applicant

    @Column(nullable = false, unique = true, length = 36)
    private String requestCode;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiresAt;

    private LocalDateTime respondedAt;
}
