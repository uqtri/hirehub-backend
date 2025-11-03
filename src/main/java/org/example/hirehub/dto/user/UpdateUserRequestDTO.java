package org.example.hirehub.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.entity.Study;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class UpdateUserRequestDTO {
    private String name;
    private String password;
    private String address;
    private Long roleId;
    private Boolean isVerified;
    private Boolean isBanned;
    private List<Long> skillIds;
    private String numberOfEmployees;
    private String description;
    private String github;
    private String resume_link;
    private List<Long> languageLevelIds;
    private MultipartFile avatar;
    private MultipartFile resume;
    private String field;

}