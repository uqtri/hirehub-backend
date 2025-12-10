package org.example.hirehub.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SeenUserDTO {
    private Long id;
    private String email;
    private String emoji;
}
