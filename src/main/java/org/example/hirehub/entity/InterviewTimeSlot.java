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
@Table(name = "interview_time_slot")
public class InterviewTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_request_id", nullable = false)
    private InterviewScheduleRequest scheduleRequest;

    @Column(nullable = false)
    private LocalDateTime proposedTime;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    private String conflictReason;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
