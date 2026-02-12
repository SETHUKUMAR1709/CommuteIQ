package com.commuteiq.platform.controller;

import com.commuteiq.platform.dto.response.ApiResponse;
import com.commuteiq.platform.dto.response.RidePlanResponse;
import com.commuteiq.platform.service.RidePlanService;
import com.commuteiq.platform.service.RidePlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ride-plans")
@RequiredArgsConstructor
public class RidePlanController {

    private final RidePlanService ridePlanService;
    private final RidePlanningService ridePlanningService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RidePlanResponse>>> getRidePlans(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RidePlanResponse> page = ridePlanService.getRidePlansByDatePaged(date, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RidePlanResponse>> getRidePlan(@PathVariable Long id) {
        RidePlanResponse response = ridePlanService.getRidePlanById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RidePlanResponse>>> generatePlans(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RidePlanResponse> responses = ridePlanningService.generatePlansForDate(date);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ride plans generated", responses));
    }
}
