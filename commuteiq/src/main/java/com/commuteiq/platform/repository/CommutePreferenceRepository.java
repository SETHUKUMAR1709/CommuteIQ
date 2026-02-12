package com.commuteiq.platform.repository;

import com.commuteiq.platform.entity.CommutePreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommutePreferenceRepository extends JpaRepository<CommutePreference, Long> {

    Optional<CommutePreference> findByEmployeeId(Long employeeId);
}
