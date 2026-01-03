package org.example.hirehub.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInterviewResultDTO {
    private Long roomId;
    private Integer score;
    private String comment;
    private String privateNotes;
    private String recommendation;
}

