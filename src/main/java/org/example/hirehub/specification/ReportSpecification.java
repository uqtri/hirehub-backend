package org.example.hirehub.specification;

import org.example.hirehub.dto.report.ReportFilter;
import org.example.hirehub.entity.Report;
import org.springframework.data.jpa.domain.Specification;

public class ReportSpecification {

    public static Specification<Report> reportSpecification(ReportFilter filter) {

        return status(filter.getStatus()).and(resourceName(filter.getResourceName()));
    }
    public static Specification<Report> status(String status) {
        return (root, query, builder) -> {
            if(status == null)
                return null;
            return builder.equal(root.get("status"), status);
        };
    }
    public static Specification<Report> resourceName(String resourceName) {
        return (root, query, builder) -> {
            if(resourceName == null)
                return null;
            return builder.equal(root.get("resourceName"), resourceName);
        };
    }
}
