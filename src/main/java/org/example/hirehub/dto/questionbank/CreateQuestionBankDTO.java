package org.example.hirehub.dto.questionbank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionBankDTO {
    private Long recruiterId;
    private String title;
    private String description;
    private String category;
    private List<String> questions; // List of question contents
}

