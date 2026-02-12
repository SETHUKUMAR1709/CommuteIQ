package com.commuteiq.platform.repository;

import com.commuteiq.platform.entity.RidePlan;
import com.commuteiq.platform.entity.RidePlanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RidePlanRepository extends JpaRepository<RidePlan, Long> {

    List<RidePlan> findByDate(LocalDate date);

    Page<RidePlan> findByDate(LocalDate date, Pageable pageable);

    List<RidePlan> findByStatus(RidePlanStatus status);

    List<RidePlan> findByDateAndStatus(LocalDate date, RidePlanStatus status);

    @Query("SELECT COALESCE(SUM(SIZE(rp.ridePlanEmployees)) * 100.0 / SUM(rp.vehicle.capacity), 0) " +
            "FROM RidePlan rp WHERE rp.date = :date")
    Double calculateOccupancyPercentageByDate(@Param("date") LocalDate date);

    @Query("SELECT rp FROM RidePlan rp WHERE rp.vehicle.id = :vehicleId AND rp.date = :date")
    List<RidePlan> findByVehicleIdAndDate(@Param("vehicleId") Long vehicleId, @Param("date") LocalDate date);

    @Query("SELECT rp FROM RidePlan rp WHERE rp.driver.id = :driverId AND rp.date = :date")
    List<RidePlan> findByDriverIdAndDate(@Param("driverId") Long driverId, @Param("date") LocalDate date);
}
