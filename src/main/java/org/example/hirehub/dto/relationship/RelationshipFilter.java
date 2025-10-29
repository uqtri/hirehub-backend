package org.example.hirehub.dto.relationship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.entity.Relationship;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipFilter {
    private String status;
    private Long senderId;
    private Long receiverId;
}
