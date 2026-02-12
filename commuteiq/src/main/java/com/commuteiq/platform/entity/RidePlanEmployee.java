package com.commuteiq.platform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ride_plan_employees", indexes = {
        @Index(name = "idx_rpe_ride_plan", columnList = "ride_plan_id"),
        @Index(name = "idx_rpe_employee", columnList = "employee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RidePlanEmployee extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_plan_id", nullable = false)
    private RidePlan ridePlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;
}
