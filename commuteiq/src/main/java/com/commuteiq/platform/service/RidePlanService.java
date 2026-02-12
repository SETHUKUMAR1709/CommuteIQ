package com.commuteiq.platform.service;

import com.commuteiq.platform.dto.response.RidePlanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RidePlanService {

    List<RidePlanResponse> getRidePlansByDate(LocalDate date);

    Page<RidePlanResponse> getRidePlansByDatePaged(LocalDate date, Pageable pageable);

    RidePlanResponse getRidePlanById(Long id);
}
