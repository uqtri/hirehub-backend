package org.example.hirehub.specification;

import org.example.hirehub.dto.relationship.RelationshipFilter;
import org.example.hirehub.entity.Relationship;
import org.springframework.data.jpa.domain.Specification;

public class RelationshipSpecification {

    public static Specification<Relationship> status(String status) {
        return (root, query, builder) ->
        {
            if (status == null)
                return null;
            return builder.equal(root.get("status"), status);
        };
    }
    public static Specification<Relationship> sender(Long senderId) {
        return (root, query, builder) -> {
            if(senderId == null)
                return null;

            return builder.equal(root.get("userA").get("id"), senderId);
        };
    }
    public static Specification<Relationship> receiver(Long receiverId) {
        return (root, query, builder) -> {
            if(receiverId == null)
                return null;

            return builder.equal(root.get("userB").get("id"), receiverId);
        };
    }

}
