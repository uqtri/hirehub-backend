package org.example.hirehub.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

public class CreateJobRequestDTO {
    @NotBlank(message = "Tiêu đề công việc không được để trống.")
    @NotNull(message = "Tiêu đề công việc là bắt buộc.")
    @Size(max = 100, message = "Tiêu đề công việc không được dài quá 100 ký tự.")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z\\- ]+", message = "Tiêu đề công việc chứa kí tự cấm!")
    private String title;

    @NotBlank(message = "Mô tả công việc không được để trống.")
    @NotNull(message = "Mô tả công việc là bắt buộc.")
    @Size(max = 3000, message = "Mô tả công việc không được dài quá 3000 ký tự.")
    private String description;

    private String applyLink;

    @NotBlank(message = "Trình độ công việc không được để trống.")
    @NotNull(message = "Trình độ công việc là bắt buộc.")
    private String level;

    @NotBlank(message = "Hình thức làm việc không được để trống.")
    @NotNull(message = "Hình thức làm việc là bắt buộc.")
    private String workspace;

    private List<Long> skillIds;
    private Long recruiterId;
}
