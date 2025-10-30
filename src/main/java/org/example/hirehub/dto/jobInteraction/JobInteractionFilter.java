package org.example.hirehub.dto.jobInteraction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobInteractionFilter {

    String interaction;
    Long userId;
}
