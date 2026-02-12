package com.commuteiq.platform.mapper;

import com.commuteiq.platform.dto.response.SafetyEventResponse;
import com.commuteiq.platform.entity.SafetyEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SafetyEventMapper {

    @Mapping(source = "ridePlan.id", target = "ridePlanId")
    @Mapping(source = "type", target = "type")
    SafetyEventResponse toResponse(SafetyEvent safetyEvent);
}
