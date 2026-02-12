package com.commuteiq.platform.controller;

import com.commuteiq.platform.dto.request.RideRequestDto;
import com.commuteiq.platform.dto.response.ApiResponse;
import com.commuteiq.platform.dto.response.RideRequestResponse;
import com.commuteiq.platform.service.RideRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ride-requests")
@RequiredArgsConstructor
public class RideRequestController {

    private final RideRequestService rideRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<RideRequestResponse>> createRequest(
            @Valid @RequestBody RideRequestDto request) {
        RideRequestResponse response = rideRequestService.createRideRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ride request created", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RideRequestResponse>> getRequest(@PathVariable Long id) {
        RideRequestResponse response = rideRequestService.getRideRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<Page<RideRequestResponse>>> getByEmployee(
            @PathVariable Long employeeId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RideRequestResponse> page = rideRequestService.getRideRequestsByEmployee(employeeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<RideRequestResponse>>> getPending(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RideRequestResponse> responses = rideRequestService.getPendingRequestsByDate(date);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<RideRequestResponse>> cancelRequest(@PathVariable Long id) {
        RideRequestResponse response = rideRequestService.cancelRideRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Ride request cancelled", response));
    }
}
