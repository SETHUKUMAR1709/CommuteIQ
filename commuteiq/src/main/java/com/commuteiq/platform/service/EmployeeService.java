package com.commuteiq.platform.service;

import com.commuteiq.platform.dto.request.EmployeeRequest;
import com.commuteiq.platform.dto.request.CommutePreferenceRequest;
import com.commuteiq.platform.dto.response.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse getEmployeeById(Long id);

    Page<EmployeeResponse> getAllEmployees(Pageable pageable);

    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);

    void deleteEmployee(Long id);

    void setCommutePreference(CommutePreferenceRequest request);
}
