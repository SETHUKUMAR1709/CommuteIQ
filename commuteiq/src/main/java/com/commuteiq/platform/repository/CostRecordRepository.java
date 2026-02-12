package com.commuteiq.platform.repository;

import com.commuteiq.platform.entity.CostRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CostRecordRepository extends JpaRepository<CostRecord, Long> {

    Optional<CostRecord> findByRidePlanId(Long ridePlanId);

    @Query("SELECT COALESCE(SUM(c.totalCost), 0) FROM CostRecord c WHERE c.ridePlan.date = :date")
    BigDecimal sumTotalCostByDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(c.fuelCost), 0) FROM CostRecord c WHERE c.ridePlan.date = :date")
    BigDecimal sumFuelCostByDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(c.driverCost), 0) FROM CostRecord c WHERE c.ridePlan.date = :date")
    BigDecimal sumDriverCostByDate(@Param("date") LocalDate date);
}
