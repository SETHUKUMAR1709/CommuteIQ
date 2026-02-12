package com.commuteiq.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cost_records", indexes = {
        @Index(name = "idx_cost_record_ride_plan", columnList = "ride_plan_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostRecord extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_plan_id", nullable = false, unique = true)
    private RidePlan ridePlan;

    @Column(name = "fuel_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal fuelCost;

    @Column(name = "driver_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal driverCost;

    @Column(name = "idle_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal idleCost;

    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;
}
