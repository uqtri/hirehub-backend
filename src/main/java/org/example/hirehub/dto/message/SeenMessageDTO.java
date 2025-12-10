package org.example.hirehub.dto.message;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SeenMessageDTO {
    private Long messageId;
    private Long userId;
}
