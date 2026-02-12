package com.commuteiq.platform.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {

    @NotBlank(message = "Plate number is required")
    @Size(max = 20)
    private String plateNumber;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 50, message = "Capacity cannot exceed 50")
    private Integer capacity;

    @NotBlank(message = "Vendor name is required")
    @Size(max = 100)
    private String vendorName;
}
