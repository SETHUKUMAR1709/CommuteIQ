package com.commuteiq.platform.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Gender is required")
    @Size(max = 10)
    private String gender;

    @NotBlank(message = "Department is required")
    @Size(max = 100)
    private String department;

    @NotNull(message = "Home latitude is required")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double homeLatitude;

    @NotNull(message = "Home longitude is required")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double homeLongitude;

    @NotBlank(message = "Office location is required")
    @Size(max = 200)
    private String officeLocation;
}
