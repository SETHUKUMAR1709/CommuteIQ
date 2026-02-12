package com.commuteiq.platform.service.impl;

import com.commuteiq.platform.dto.response.RidePlanResponse;
import com.commuteiq.platform.entity.RidePlan;
import com.commuteiq.platform.exception.ResourceNotFoundException;
import com.commuteiq.platform.repository.RidePlanRepository;
import com.commuteiq.platform.service.RidePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RidePlanServiceImpl implements RidePlanService {

    private final RidePlanRepository ridePlanRepository;

    @Override
    public List<RidePlanResponse> getRidePlansByDate(LocalDate date) {
        return ridePlanRepository.findByDate(date).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RidePlanResponse> getRidePlansByDatePaged(LocalDate date, Pageable pageable) {
        return ridePlanRepository.findByDate(date, pageable)
                .map(this::toResponse);
    }

    @Override
    public RidePlanResponse getRidePlanById(Long id) {
        RidePlan ridePlan = ridePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RidePlan", "id", id));
        return toResponse(ridePlan);
    }

    private RidePlanResponse toResponse(RidePlan ridePlan) {
        List<RidePlanResponse.RidePlanEmployeeResponse> employees = ridePlan.getRidePlanEmployees()
                .stream()
                .map(rpe -> RidePlanResponse.RidePlanEmployeeResponse.builder()
                        .employeeId(rpe.getEmployee().getId())
                        .employeeName(rpe.getEmployee().getName())
                        .stopOrder(rpe.getStopOrder())
                        .build())
                .collect(Collectors.toList());

        return RidePlanResponse.builder()
                .id(ridePlan.getId())
                .vehicleId(ridePlan.getVehicle().getId())
                .vehiclePlateNumber(ridePlan.getVehicle().getPlateNumber())
                .driverId(ridePlan.getDriver().getId())
                .driverName(ridePlan.getDriver().getName())
                .date(ridePlan.getDate())
                .estimatedDistance(ridePlan.getEstimatedDistance())
                .estimatedDuration(ridePlan.getEstimatedDuration())
                .status(ridePlan.getStatus().name())
                .employees(employees)
                .createdAt(ridePlan.getCreatedAt())
                .build();
    }
}
