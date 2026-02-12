package com.commuteiq.platform.controller;

import com.commuteiq.platform.dto.response.ApiResponse;
import com.commuteiq.platform.dto.response.SafetyEventResponse;
import com.commuteiq.platform.entity.SafetyEventType;
import com.commuteiq.platform.service.SafetyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/safety-events")
@RequiredArgsConstructor
public class SafetyController {

    private final SafetyService safetyService;

    @GetMapping("/ride-plan/{ridePlanId}")
    public ResponseEntity<ApiResponse<List<SafetyEventResponse>>> getByRidePlan(
            @PathVariable Long ridePlanId) {
        List<SafetyEventResponse> responses = safetyService.getEventsByRidePlan(ridePlanId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SafetyEventResponse>>> getByType(
            @RequestParam SafetyEventType type,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SafetyEventResponse> page = safetyService.getEventsByType(type, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }
}
