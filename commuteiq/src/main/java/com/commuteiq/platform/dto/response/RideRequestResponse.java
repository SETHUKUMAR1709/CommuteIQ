package com.commuteiq.platform.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate requestDate;
    private String status;
    private java.time.LocalDateTime createdAt;
}
