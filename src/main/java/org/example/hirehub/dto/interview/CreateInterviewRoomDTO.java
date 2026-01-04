package org.example.hirehub.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInterviewRoomDTO {
    private Long jobId;
    private Long applicantId;
    private Long recruiterId;
    private LocalDateTime scheduledTime;
    private Integer durationMinutes; // Thời lượng phỏng vấn (phút)
    private String interviewType; // CHAT, VIDEO
    private String interviewMode; // LIVE, ASYNC
    private Integer roundNumber;
    private Long previousRoomId;
    private List<Long> selectedQuestionIds; // IDs của câu hỏi được chọn (cho ASYNC mode)
}

