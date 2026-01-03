package org.example.hirehub.dto.questionbank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankDTO {
    private Long id;
    private Long recruiterId;
    private String recruiterName;
    private String title;
    private String description;
    private String category;
    private List<QuestionDTO> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

