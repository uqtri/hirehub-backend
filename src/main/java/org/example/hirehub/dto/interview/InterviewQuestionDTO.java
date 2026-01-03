package org.example.hirehub.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionDTO {
    private Long id;
    private Long roomId;
    private Long questionId;
    private String questionContent;
    private String answer;
    private Integer orderIndex;
    private LocalDateTime askedAt;
    private LocalDateTime answeredAt;
    private String status;
}

