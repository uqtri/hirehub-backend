package org.example.hirehub.dto.relationship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRelationshipRequestDTO {
    private Long senderId;
    private Long receiverId;

}
