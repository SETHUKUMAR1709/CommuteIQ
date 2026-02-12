package com.commuteiq.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ride_requests", indexes = {
        @Index(name = "idx_ride_request_status_date", columnList = "status, request_date"),
        @Index(name = "idx_ride_request_employee", columnList = "employee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequest extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RideRequestStatus status = RideRequestStatus.PENDING;
}
