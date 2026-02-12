package com.commuteiq.platform.service;

import com.commuteiq.platform.dto.response.DailyCostResponse;
import com.commuteiq.platform.dto.response.OccupancyResponse;
import com.commuteiq.platform.dto.response.SafetyCountResponse;

import java.time.LocalDate;

public interface AnalyticsService {

    OccupancyResponse getOccupancy(LocalDate date);

    SafetyCountResponse getSafetyCounts();

    DailyCostResponse getDailyCost(LocalDate date);
}
