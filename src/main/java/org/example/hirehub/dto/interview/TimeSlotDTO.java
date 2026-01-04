package org.example.hirehub.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    private Long id;
    private LocalDateTime proposedTime;
    private Boolean isAvailable;
    private String conflictReason;
}
