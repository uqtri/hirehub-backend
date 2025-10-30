package org.example.hirehub.dto.report;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReportRequestDTO {

    @NotNull
    Long resourceId;
    @NotNull
    String resourceName;
    @NotNull
    String reason;
    @NotNull
    Long reporterId;
}
