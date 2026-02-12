package com.commuteiq.platform.service.impl;

import com.commuteiq.platform.dto.response.DailyCostResponse;
import com.commuteiq.platform.dto.response.OccupancyResponse;
import com.commuteiq.platform.dto.response.SafetyCountResponse;
import com.commuteiq.platform.repository.CostRecordRepository;
import com.commuteiq.platform.repository.RidePlanRepository;
import com.commuteiq.platform.repository.SafetyEventRepository;
import com.commuteiq.platform.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final RidePlanRepository ridePlanRepository;
    private final SafetyEventRepository safetyEventRepository;
    private final CostRecordRepository costRecordRepository;

    @Override
    @Cacheable(value = "occupancy", key = "#date")
    public OccupancyResponse getOccupancy(LocalDate date) {
        Double occupancy = ridePlanRepository.calculateOccupancyPercentageByDate(date);
        long totalPlans = ridePlanRepository.findByDate(date).size();

        return OccupancyResponse.builder()
                .date(date)
                .occupancyPercentage(occupancy != null ? occupancy : 0.0)
                .totalRidePlans(totalPlans)
                .build();
    }

    @Override
    @Cacheable("safetyCounts")
    public SafetyCountResponse getSafetyCounts() {
        List<Object[]> grouped = safetyEventRepository.countGroupedByType();
        Map<String, Long> countsByType = new LinkedHashMap<>();
        long total = 0;

        for (Object[] row : grouped) {
            String type = row[0].toString();
            Long count = (Long) row[1];
            countsByType.put(type, count);
            total += count;
        }

        return SafetyCountResponse.builder()
                .totalEvents(total)
                .countsByType(countsByType)
                .build();
    }

    @Override
    @Cacheable(value = "dailyCost", key = "#date")
    public DailyCostResponse getDailyCost(LocalDate date) {
        return DailyCostResponse.builder()
                .date(date)
                .totalCost(costRecordRepository.sumTotalCostByDate(date))
                .fuelCost(costRecordRepository.sumFuelCostByDate(date))
                .driverCost(costRecordRepository.sumDriverCostByDate(date))
                .build();
    }
}
