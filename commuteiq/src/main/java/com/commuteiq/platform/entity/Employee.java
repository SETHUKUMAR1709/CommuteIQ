package com.commuteiq.platform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employee_department", columnList = "department"),
        @Index(name = "idx_employee_office", columnList = "office_location"),
        @Index(name = "idx_employee_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "home_latitude", nullable = false)
    private Double homeLatitude;

    @Column(name = "home_longitude", nullable = false)
    private Double homeLongitude;

    @Column(name = "office_location", nullable = false, length = 200)
    private String officeLocation;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CommutePreference commutePreference;
}
