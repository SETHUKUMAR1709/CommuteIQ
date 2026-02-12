package com.commuteiq.platform.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String name;
    private String gender;
    private String department;
    private Double homeLatitude;
    private Double homeLongitude;
    private String officeLocation;
    private Boolean active;
    private LocalDateTime createdAt;
}
