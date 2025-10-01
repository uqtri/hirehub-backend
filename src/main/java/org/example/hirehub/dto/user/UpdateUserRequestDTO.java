package org.example.hirehub.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class UpdateUserRequestDTO {
    private String email;
    private String password;
    private String address;
    private Long roleId;
    private Boolean isVerified;
    private Boolean isBanned;
    private List<Long> skillIds;
}