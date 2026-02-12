package com.commuteiq.platform.service;

import com.commuteiq.platform.dto.response.SafetyEventResponse;
import com.commuteiq.platform.entity.SafetyEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SafetyService {

    SafetyEventResponse recordSafetyEvent(Long ridePlanId, SafetyEventType type, String description);

    List<SafetyEventResponse> getEventsByRidePlan(Long ridePlanId);

    Page<SafetyEventResponse> getEventsByType(SafetyEventType type, Pageable pageable);

    long countByType(SafetyEventType type);
}
