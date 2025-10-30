package org.example.hirehub.specification;

import org.example.hirehub.entity.JobInteraction;
import org.springframework.data.jpa.domain.Specification;

public class JobInteractionSpecification {

    public static Specification<JobInteraction> interactionType(String interactionType) {

        return (root, query, builder) -> {
            if(interactionType == null)
                return null;

            return builder.equal(root.get("interaction"), interactionType);
        };
    }
    public static Specification<JobInteraction> userId(Long userId) {

        return (root, query, builder) -> {
            if(userId == null)
                return null;

            return builder.equal(root.get("user").get("id"), userId);
        };
    }
    public static Specification<JobInteraction> jobId(Long jobId) {
        return (root, query, builder) -> {
            if(jobId == null)
                return null;

            return builder.equal(root.get("job").get("id"), jobId);
        };
    }
}
