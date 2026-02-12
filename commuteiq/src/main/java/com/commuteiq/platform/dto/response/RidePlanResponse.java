package com.commuteiq.platform.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RidePlanResponse {
    private Long id;
    private Long vehicleId;
    private String vehiclePlateNumber;
    private Long driverId;
    private String driverName;
    private LocalDate date;
    private Double estimatedDistance;
    private Double estimatedDuration;
    private String status;
    private List<RidePlanEmployeeResponse> employees;
    private java.time.LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RidePlanEmployeeResponse {
        private Long employeeId;
        private String employeeName;
        private Integer stopOrder;
    }
}
