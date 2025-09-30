package org.example.hirehub.dto.experience;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateExperienceForm {

    private Long userId;
    private Long companyId;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private MultipartFile image;
}
