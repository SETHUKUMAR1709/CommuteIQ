package com.commuteiq.platform.service;

import com.commuteiq.platform.dto.request.RideRequestDto;
import com.commuteiq.platform.dto.response.RideRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RideRequestService {

    RideRequestResponse createRideRequest(RideRequestDto request);

    RideRequestResponse getRideRequestById(Long id);

    Page<RideRequestResponse> getRideRequestsByEmployee(Long employeeId, Pageable pageable);

    List<RideRequestResponse> getPendingRequestsByDate(LocalDate date);

    RideRequestResponse cancelRideRequest(Long id);
}
