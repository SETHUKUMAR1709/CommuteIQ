package com.commuteiq.platform.service.impl;

import com.commuteiq.platform.dto.request.RideRequestDto;
import com.commuteiq.platform.dto.response.RideRequestResponse;
import com.commuteiq.platform.entity.Employee;
import com.commuteiq.platform.entity.RideRequest;
import com.commuteiq.platform.entity.RideRequestStatus;
import com.commuteiq.platform.exception.InvalidOperationException;
import com.commuteiq.platform.exception.ResourceNotFoundException;
import com.commuteiq.platform.mapper.RideRequestMapper;
import com.commuteiq.platform.repository.EmployeeRepository;
import com.commuteiq.platform.repository.RideRequestRepository;
import com.commuteiq.platform.service.RideRequestService;
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
@Transactional
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final RideRequestMapper rideRequestMapper;

    @Override
    public RideRequestResponse createRideRequest(RideRequestDto request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));

        RideRequest rideRequest = RideRequest.builder()
                .employee(employee)
                .requestDate(request.getRequestDate())
                .status(RideRequestStatus.PENDING)
                .build();

        RideRequest saved = rideRequestRepository.save(rideRequest);
        return rideRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RideRequestResponse getRideRequestById(Long id) {
        RideRequest rideRequest = rideRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "id", id));
        return rideRequestMapper.toResponse(rideRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RideRequestResponse> getRideRequestsByEmployee(Long employeeId, Pageable pageable) {
        return rideRequestRepository.findByEmployeeId(employeeId, pageable)
                .map(rideRequestMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideRequestResponse> getPendingRequestsByDate(LocalDate date) {
        return rideRequestRepository.findByStatusAndRequestDate(RideRequestStatus.PENDING, date)
                .stream()
                .map(rideRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RideRequestResponse cancelRideRequest(Long id) {
        RideRequest rideRequest = rideRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "id", id));

        if (rideRequest.getStatus() != RideRequestStatus.PENDING) {
            throw new InvalidOperationException("Only PENDING ride requests can be cancelled");
        }

        rideRequest.setStatus(RideRequestStatus.CANCELLED);
        RideRequest saved = rideRequestRepository.save(rideRequest);
        return rideRequestMapper.toResponse(saved);
    }
}
