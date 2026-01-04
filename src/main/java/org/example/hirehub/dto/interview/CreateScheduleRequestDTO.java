package org.example.hirehub.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleRequestDTO {
    private Long jobId;
    private Long applicantId;
    private Long recruiterId;
    private List<LocalDateTime> proposedTimeSlots; // 3-5 time options
    private String interviewType; // CHAT, VIDEO
    private String interviewMode; // LIVE, ASYNC
    private Integer roundNumber;
    private Long previousRoomId;
    private List<Long> selectedQuestionIds; // For ASYNC mode
    private Integer expirationHours; // Optional, default 48 hours
}
