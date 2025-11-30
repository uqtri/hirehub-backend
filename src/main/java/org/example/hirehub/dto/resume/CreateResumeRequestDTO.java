package org.example.hirehub.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class CreateResumeRequestDTO {
//    @NotBlank(message = "Resume không được để trống.")
//    @NotNull(message = "Resume là bắt buộc.")
//    private String link;

//    @NotNull
    private MultipartFile resumeFile;

    @Size(max = 500, message = "Thư giới thiệu không được dài quá 500 ký tự.")
    private String cover_letter;

    private Long jobId;
    private Long userId;
    private String openAiResumeId;
    private String link;
}
