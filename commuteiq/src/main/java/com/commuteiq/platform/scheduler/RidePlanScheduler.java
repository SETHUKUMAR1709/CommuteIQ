package com.commuteiq.platform.scheduler;

import com.commuteiq.platform.service.RidePlanningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Daily scheduler that automatically generates ride plans for the next day
 * from all pending ride requests. Runs at 10:00 PM every day.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RidePlanScheduler {

    private final RidePlanningService ridePlanningService;

    @Scheduled(cron = "0 0 22 * * *")
    public void generateNextDayRidePlans() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        log.info("Scheduler triggered: generating ride plans for {}", tomorrow);

        try {
            var plans = ridePlanningService.generatePlansForDate(tomorrow);
            log.info("Scheduler completed: {} ride plans generated for {}", plans.size(), tomorrow);
        } catch (Exception e) {
            log.error("Scheduler failed for date {}: {}", tomorrow, e.getMessage(), e);
        }
    }
}
