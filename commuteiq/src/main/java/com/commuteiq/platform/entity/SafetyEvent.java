package com.commuteiq.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "safety_events", indexes = {
        @Index(name = "idx_safety_event_type", columnList = "type"),
        @Index(name = "idx_safety_event_ride_plan", columnList = "ride_plan_id"),
        @Index(name = "idx_safety_event_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SafetyEvent extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_plan_id", nullable = false)
    private RidePlan ridePlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SafetyEventType type;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 500)
    private String description;
}
