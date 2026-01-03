package org.example.hirehub.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResultDTO {
    private Long id;
    private Long roomId;
    private Integer score;
    private String comment;
    private String privateNotes;
    private String recommendation;
    private LocalDateTime createdAt;
}

