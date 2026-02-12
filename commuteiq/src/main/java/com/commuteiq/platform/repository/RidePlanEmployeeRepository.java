package com.commuteiq.platform.repository;

import com.commuteiq.platform.entity.RidePlanEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RidePlanEmployeeRepository extends JpaRepository<RidePlanEmployee, Long> {

    List<RidePlanEmployee> findByRidePlanId(Long ridePlanId);

    List<RidePlanEmployee> findByEmployeeId(Long employeeId);
}
