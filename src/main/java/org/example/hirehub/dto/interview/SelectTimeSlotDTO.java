package org.example.hirehub.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectTimeSlotDTO {
    private Long scheduleRequestId;
    private Long timeSlotId;
    private Long applicantId; // For verification
}
