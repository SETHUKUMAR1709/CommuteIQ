package com.commuteiq.platform.service;

import com.commuteiq.platform.dto.response.CostRecordResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CostService {

    CostRecordResponse recordCost(Long ridePlanId, BigDecimal fuelCost, BigDecimal driverCost, BigDecimal idleCost);

    CostRecordResponse getCostByRidePlan(Long ridePlanId);

    BigDecimal getDailyCost(LocalDate date);
}
