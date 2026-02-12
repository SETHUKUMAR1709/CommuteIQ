package com.commuteiq.platform.repository;

import com.commuteiq.platform.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByActiveTrue();

    Optional<Driver> findByLicenseNumber(String licenseNumber);
}
