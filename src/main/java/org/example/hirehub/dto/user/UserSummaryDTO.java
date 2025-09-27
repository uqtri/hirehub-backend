package org.example.hirehub.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryDTO {
    private Long id;
    private String email;
    private String name;
    private String avatar;
}