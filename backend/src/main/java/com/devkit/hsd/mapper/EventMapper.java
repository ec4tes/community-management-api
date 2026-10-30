package com.devkit.hsd.mapper;

import com.devkit.hsd.dto.request.CreateEventRequest;
import com.devkit.hsd.dto.response.EventResponse;
import com.devkit.hsd.entity.Event;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "qrCodeData", ignore = true)
    @Mapping(target = "qrExpiresAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event toEntity(CreateEventRequest request);

    @Mapping(target = "communityId", source = "community.id")
    EventResponse toResponse(Event event);

    List<EventResponse> toEventResponseList(List<Event> events);
}