package com.commuteiq.platform.dto.response;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SafetyCountResponse {
    private long totalEvents;
    private Map<String, Long> countsByType;
}
