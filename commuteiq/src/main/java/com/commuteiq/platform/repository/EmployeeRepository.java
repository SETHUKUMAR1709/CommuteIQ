package com.commuteiq.platform.repository;

import com.commuteiq.platform.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByActiveTrue(Pageable pageable);

    List<Employee> findByDepartment(String department);

    List<Employee> findByOfficeLocation(String officeLocation);

    Optional<Employee> findByUserId(Long userId);

    List<Employee> findByActiveTrue();
}
