package com.commuteiq.platform.mapper;

import com.commuteiq.platform.dto.response.CostRecordResponse;
import com.commuteiq.platform.entity.CostRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CostRecordMapper {

    @Mapping(source = "ridePlan.id", target = "ridePlanId")
    CostRecordResponse toResponse(CostRecord costRecord);
}
