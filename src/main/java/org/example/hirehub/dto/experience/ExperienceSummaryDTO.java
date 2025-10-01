package org.example.hirehub.dto.experience;

import org.example.hirehub.dto.user.UserSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceSummaryDTO {
    private Long id;
    private String position;
    private UserSummaryDTO company;
}