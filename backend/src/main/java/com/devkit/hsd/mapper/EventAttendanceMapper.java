package com.devkit.hsd.mapper;

import com.devkit.hsd.dto.response.EventAttendanceResponse;
import com.devkit.hsd.entity.EventAttendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventAttendanceMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    @Mapping(target = "message", constant = "Etkinlik katılımı başarıyla kaydedildi.")
    EventAttendanceResponse toResponse(EventAttendance eventAttendance);
}