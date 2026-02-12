package com.commuteiq.platform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_vehicle_plate", columnList = "plate_number", unique = true),
        @Index(name = "idx_vehicle_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "vendor_name", nullable = false, length = 100)
    private String vendorName;

    @Column(nullable = false)
    private Boolean active = true;
}
