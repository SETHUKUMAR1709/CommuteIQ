package com.commuteiq.platform.mapper;

import com.commuteiq.platform.dto.request.EmployeeRequest;
import com.commuteiq.platform.dto.response.EmployeeResponse;
import com.commuteiq.platform.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeResponse toResponse(Employee employee);

    Employee toEntity(EmployeeRequest request);

    void updateEntity(EmployeeRequest request, @MappingTarget Employee employee);
}
