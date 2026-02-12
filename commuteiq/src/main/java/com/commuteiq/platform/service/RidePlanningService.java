package com.commuteiq.platform.service;

import com.commuteiq.platform.dto.response.RidePlanResponse;

import java.time.LocalDate;
import java.util.List;

public interface RidePlanningService {

    List<RidePlanResponse> generatePlansForDate(LocalDate date);
}
