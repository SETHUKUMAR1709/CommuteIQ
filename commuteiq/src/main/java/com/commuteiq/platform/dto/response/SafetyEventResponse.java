package com.commuteiq.platform.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SafetyEventResponse {
    private Long id;
    private Long ridePlanId;
    private String type;
    private LocalDateTime timestamp;
    private String description;
}
