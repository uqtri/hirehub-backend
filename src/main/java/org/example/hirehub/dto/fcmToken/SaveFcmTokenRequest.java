package org.example.hirehub.dto.fcmToken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SaveFcmTokenRequest {
    private Long userId;
    private String token;
}
