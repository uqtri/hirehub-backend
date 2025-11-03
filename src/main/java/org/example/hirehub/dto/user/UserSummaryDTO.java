package org.example.hirehub.dto.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class UserSummaryDTO {
    private Long id;
    private String email;
    private String name;
    private String address;
    private String avatar;
    private String position;
    private String status;
}