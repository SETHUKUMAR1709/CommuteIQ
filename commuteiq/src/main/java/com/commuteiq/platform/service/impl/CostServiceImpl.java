package com.commuteiq.platform.service.impl;

import com.commuteiq.platform.dto.response.CostRecordResponse;
import com.commuteiq.platform.entity.CostRecord;
import com.commuteiq.platform.entity.RidePlan;
import com.commuteiq.platform.exception.ResourceNotFoundException;
import com.commuteiq.platform.mapper.CostRecordMapper;
import com.commuteiq.platform.repository.CostRecordRepository;
import com.commuteiq.platform.repository.RidePlanRepository;
import com.commuteiq.platform.service.CostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class CostServiceImpl implements CostService {

    private final CostRecordRepository costRecordRepository;
    private final RidePlanRepository ridePlanRepository;
    private final CostRecordMapper costRecordMapper;

    @Override
    public CostRecordResponse recordCost(Long ridePlanId, BigDecimal fuelCost, BigDecimal driverCost,
            BigDecimal idleCost) {
        RidePlan ridePlan = ridePlanRepository.findById(ridePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RidePlan", "id", ridePlanId));

        BigDecimal totalCost = fuelCost.add(driverCost).add(idleCost);

        CostRecord costRecord = CostRecord.builder()
                .ridePlan(ridePlan)
                .fuelCost(fuelCost)
                .driverCost(driverCost)
                .idleCost(idleCost)
                .totalCost(totalCost)
                .build();

        CostRecord saved = costRecordRepository.save(costRecord);
        return costRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CostRecordResponse getCostByRidePlan(Long ridePlanId) {
        CostRecord record = costRecordRepository.findByRidePlanId(ridePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("CostRecord", "ridePlanId", ridePlanId));
        return costRecordMapper.toResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getDailyCost(LocalDate date) {
        return costRecordRepository.sumTotalCostByDate(date);
    }
}
