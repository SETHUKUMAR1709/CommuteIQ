package com.commuteiq.platform.repository;

import com.commuteiq.platform.entity.SafetyEvent;
import com.commuteiq.platform.entity.SafetyEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SafetyEventRepository extends JpaRepository<SafetyEvent, Long> {

    List<SafetyEvent> findByRidePlanId(Long ridePlanId);

    Page<SafetyEvent> findByType(SafetyEventType type, Pageable pageable);

    long countByType(SafetyEventType type);

    @Query("SELECT se.type, COUNT(se) FROM SafetyEvent se GROUP BY se.type")
    List<Object[]> countGroupedByType();

    @Query("SELECT COUNT(se) FROM SafetyEvent se WHERE se.timestamp BETWEEN :start AND :end")
    long countByTimestampBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
