package org.example.hirehub.dto.study;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateStudyRequestDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private String major;
    private String degree;
    private Long universityId;
    private Long userId;
}
