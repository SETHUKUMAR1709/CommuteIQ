package com.commuteiq.platform.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Request date is required")
    @FutureOrPresent(message = "Request date must be today or in the future")
    private LocalDate requestDate;
}
