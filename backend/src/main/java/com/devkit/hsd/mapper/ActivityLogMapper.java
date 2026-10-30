package com.devkit.hsd.mapper;

import com.devkit.hsd.dto.response.MyLogResponse;
import com.devkit.hsd.entity.ActivityLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {

    MyLogResponse toMyLogResponse(ActivityLog activityLog);
}