package org.example.hirehub.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

/**
 * DTO for creating draft jobs with minimal validation.
 * Only title is required, all other fields are optional.
 */
public class CreateDraftJobRequestDTO {
    @NotBlank(message = "Tiêu đề công việc không được để trống.")
    @NotNull(message = "Tiêu đề công việc là bắt buộc.")
    @Size(max = 100, message = "Tiêu đề công việc không được dài quá 100 ký tự.")
    private String title;

    @Size(max = 3000, message = "Mô tả công việc không được dài quá 3000 ký tự.")
    private String description;

    private String applyLink;
    private String level;
    private String workspace;
    private List<Long> skillIds;
    private Long recruiterId;
    private String type;
    private String address;
}
