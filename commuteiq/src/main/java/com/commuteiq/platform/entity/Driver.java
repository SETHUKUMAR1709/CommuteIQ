package com.commuteiq.platform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers", indexes = {
        @Index(name = "idx_driver_license", columnList = "license_number", unique = true),
        @Index(name = "idx_driver_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(nullable = false)
    private Double rating = 5.0;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
