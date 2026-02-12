package com.commuteiq.platform.repository;

import com.commuteiq.platform.entity.RideRequest;
import com.commuteiq.platform.entity.RideRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {

    List<RideRequest> findByStatusAndRequestDate(RideRequestStatus status, LocalDate requestDate);

    Page<RideRequest> findByEmployeeId(Long employeeId, Pageable pageable);

    List<RideRequest> findByRequestDate(LocalDate requestDate);

    long countByStatusAndRequestDate(RideRequestStatus status, LocalDate requestDate);
}
