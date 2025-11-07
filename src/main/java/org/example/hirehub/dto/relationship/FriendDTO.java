package org.example.hirehub.dto.relationship;

import lombok.Data;
import org.example.hirehub.dto.user.UserDetailDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;

@Data
public class FriendDTO {
    UserDetailDTO user;
}
