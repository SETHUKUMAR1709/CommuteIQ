package com.commuteiq.platform.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostRecordResponse {
    private Long id;
    private Long ridePlanId;
    private BigDecimal fuelCost;
    private BigDecimal driverCost;
    private BigDecimal idleCost;
    private BigDecimal totalCost;
}
