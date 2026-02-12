package com.commuteiq.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ride_plans", indexes = {
        @Index(name = "idx_ride_plan_date", columnList = "date"),
        @Index(name = "idx_ride_plan_status", columnList = "status"),
        @Index(name = "idx_ride_plan_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_ride_plan_driver", columnList = "driver_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RidePlan extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "estimated_distance")
    private Double estimatedDistance;

    @Column(name = "estimated_duration")
    private Double estimatedDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RidePlanStatus status = RidePlanStatus.SCHEDULED;

    @OneToMany(mappedBy = "ridePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RidePlanEmployee> ridePlanEmployees = new ArrayList<>();

    @OneToMany(mappedBy = "ridePlan", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SafetyEvent> safetyEvents = new ArrayList<>();

    @OneToOne(mappedBy = "ridePlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CostRecord costRecord;
}
