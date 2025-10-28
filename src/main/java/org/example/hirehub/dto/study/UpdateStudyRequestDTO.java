package org.example.hirehub.dto.study;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudyRequestDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private String major;
    private String degree;
    private Long universityId;
}
