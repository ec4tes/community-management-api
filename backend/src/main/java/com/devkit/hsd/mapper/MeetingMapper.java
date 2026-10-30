package com.devkit.hsd.mapper;

import com.devkit.hsd.dto.request.CreateMeetingRequest;
import com.devkit.hsd.dto.response.MeetingResponse;
import com.devkit.hsd.entity.Meeting;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeetingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "qrCodeData", ignore = true)
    @Mapping(target = "qrExpiresAt", ignore = true)
    @Mapping(target = "committee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Meeting toEntity(CreateMeetingRequest request);

    @Mapping(target = "committeeId", source = "committee.id")
    @Mapping(target = "communityId", source = "community.id")
    MeetingResponse toResponse(Meeting meeting);

    List<MeetingResponse> toResponseList(List<Meeting> meetings);
}