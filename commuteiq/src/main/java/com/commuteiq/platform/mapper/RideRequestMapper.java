package com.commuteiq.platform.mapper;

import com.commuteiq.platform.dto.response.RideRequestResponse;
import com.commuteiq.platform.entity.RideRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RideRequestMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.name", target = "employeeName")
    @Mapping(source = "status", target = "status")
    RideRequestResponse toResponse(RideRequest rideRequest);
}
