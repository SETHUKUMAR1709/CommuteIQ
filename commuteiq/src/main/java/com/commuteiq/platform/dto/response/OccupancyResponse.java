package com.commuteiq.platform.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupancyResponse {
    private LocalDate date;
    private Double occupancyPercentage;
    private long totalRidePlans;
}
