package com.commuteiq.platform.service.impl;

import com.commuteiq.platform.dto.request.CommutePreferenceRequest;
import com.commuteiq.platform.dto.request.EmployeeRequest;
import com.commuteiq.platform.dto.response.EmployeeResponse;
import com.commuteiq.platform.entity.CommutePreference;
import com.commuteiq.platform.entity.Employee;
import com.commuteiq.platform.exception.ResourceNotFoundException;
import com.commuteiq.platform.mapper.EmployeeMapper;
import com.commuteiq.platform.repository.CommutePreferenceRepository;
import com.commuteiq.platform.repository.EmployeeRepository;
import com.commuteiq.platform.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CommutePreferenceRepository commutePreferenceRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = employeeMapper.toEntity(request);
        employee.setActive(true);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return employeeRepository.findByActiveTrue(pageable)
                .map(employeeMapper::toResponse);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employeeMapper.updateEntity(request, employee);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    @Override
    public void setCommutePreference(CommutePreferenceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));

        CommutePreference preference = commutePreferenceRepository
                .findByEmployeeId(request.getEmployeeId())
                .orElse(new CommutePreference());

        preference.setEmployee(employee);
        preference.setPickupStartTime(request.getPickupStartTime());
        preference.setPickupEndTime(request.getPickupEndTime());
        preference.setMaxWalkDistance(request.getMaxWalkDistance());
        preference.setSameGenderRequired(request.getSameGenderRequired());
        preference.setWorkFromHomeDays(request.getWorkFromHomeDays());

        commutePreferenceRepository.save(preference);
    }
}
