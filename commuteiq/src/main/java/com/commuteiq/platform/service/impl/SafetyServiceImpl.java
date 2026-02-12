package com.commuteiq.platform.service.impl;

import com.commuteiq.platform.dto.response.SafetyEventResponse;
import com.commuteiq.platform.entity.RidePlan;
import com.commuteiq.platform.entity.SafetyEvent;
import com.commuteiq.platform.entity.SafetyEventType;
import com.commuteiq.platform.exception.ResourceNotFoundException;
import com.commuteiq.platform.mapper.SafetyEventMapper;
import com.commuteiq.platform.repository.RidePlanRepository;
import com.commuteiq.platform.repository.SafetyEventRepository;
import com.commuteiq.platform.service.SafetyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SafetyServiceImpl implements SafetyService {

    private final SafetyEventRepository safetyEventRepository;
    private final RidePlanRepository ridePlanRepository;
    private final SafetyEventMapper safetyEventMapper;

    @Override
    public SafetyEventResponse recordSafetyEvent(Long ridePlanId, SafetyEventType type, String description) {
        RidePlan ridePlan = ridePlanRepository.findById(ridePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RidePlan", "id", ridePlanId));

        SafetyEvent event = SafetyEvent.builder()
                .ridePlan(ridePlan)
                .type(type)
                .timestamp(LocalDateTime.now())
                .description(description)
                .build();

        SafetyEvent saved = safetyEventRepository.save(event);
        return safetyEventMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SafetyEventResponse> getEventsByRidePlan(Long ridePlanId) {
        return safetyEventRepository.findByRidePlanId(ridePlanId).stream()
                .map(safetyEventMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SafetyEventResponse> getEventsByType(SafetyEventType type, Pageable pageable) {
        return safetyEventRepository.findByType(type, pageable)
                .map(safetyEventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByType(SafetyEventType type) {
        return safetyEventRepository.countByType(type);
    }
}
