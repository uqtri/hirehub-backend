package org.example.hirehub.dto.experience;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class  CreateExperienceForm {

    private Long userId;
    private Long companyId;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private MultipartFile image;
    private String type;
}
