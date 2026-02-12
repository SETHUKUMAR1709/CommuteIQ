package com.commuteiq.platform.controller;

import com.commuteiq.platform.dto.response.*;
import com.commuteiq.platform.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/occupancy")
    public ResponseEntity<ApiResponse<OccupancyResponse>> getOccupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        OccupancyResponse response = analyticsService.getOccupancy(date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/safety-count")
    public ResponseEntity<ApiResponse<SafetyCountResponse>> getSafetyCount() {
        SafetyCountResponse response = analyticsService.getSafetyCounts();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/daily-cost")
    public ResponseEntity<ApiResponse<DailyCostResponse>> getDailyCost(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyCostResponse response = analyticsService.getDailyCost(date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
