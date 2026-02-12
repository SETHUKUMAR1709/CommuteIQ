package com.commuteiq.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "commute_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommutePreference extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "pickup_start_time", nullable = false)
    private LocalTime pickupStartTime;

    @Column(name = "pickup_end_time", nullable = false)
    private LocalTime pickupEndTime;

    @Column(name = "max_walk_distance")
    private Double maxWalkDistance;

    @Column(name = "same_gender_required", nullable = false)
    private Boolean sameGenderRequired = false;

    @Column(name = "work_from_home_days", length = 100)
    private String workFromHomeDays;
}
