package com.commuteiq.platform.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyCostResponse {
    private LocalDate date;
    private BigDecimal totalCost;
    private BigDecimal fuelCost;
    private BigDecimal driverCost;
}
