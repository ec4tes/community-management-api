package com.devkit.hsd.mapper;

import com.devkit.hsd.dto.response.MeetingAttendanceResponse;
import com.devkit.hsd.entity.MeetingAttendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeetingAttendanceMapper {

    @Mapping(target = "meetingId", source = "meeting.id")
    @Mapping(target = "meetingTitle", source = "meeting.title")
    @Mapping(target = "message", constant = "Toplantı katılımı başarıyla kaydedildi.")
    MeetingAttendanceResponse toResponse(MeetingAttendance meetingAttendance);
}