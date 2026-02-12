package com.commuteiq.platform.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommutePreferenceRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Pickup start time is required")
    private LocalTime pickupStartTime;

    @NotNull(message = "Pickup end time is required")
    private LocalTime pickupEndTime;

    @Positive(message = "Max walk distance must be positive")
    private Double maxWalkDistance;

    private Boolean sameGenderRequired = false;

    @Size(max = 100)
    private String workFromHomeDays;
}
