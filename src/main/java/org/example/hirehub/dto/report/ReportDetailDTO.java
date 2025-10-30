package org.example.hirehub.dto.report;

import lombok.Data;
import org.example.hirehub.dto.user.UserSummaryDTO;

@Data
public class ReportDetailDTO {
    Long id;
    Object resource;
    String reason;
    String status;
    UserSummaryDTO reporter;
}
