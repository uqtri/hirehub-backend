package org.example.hirehub.dto.relationship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.user.UserSummaryDTO;
import org.example.hirehub.key.RelationshipKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipSummaryDTO {
//    private RelationshipKey id;
    private UserSummaryDTO userA;
    private UserSummaryDTO userB;
    private String status;
}
