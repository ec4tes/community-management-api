package com.devkit.hsd.service.abs;

import java.util.List;


import com.devkit.hsd.dto.request.CreateEventRequest;
import com.devkit.hsd.dto.request.EventAttendanceScanRequest;
import com.devkit.hsd.dto.request.UpdateEventRequest;
import com.devkit.hsd.dto.response.AttendanceListItemResponse;
import com.devkit.hsd.dto.response.EventAttendanceResponse;
import com.devkit.hsd.dto.response.EventResponse;

public interface IEventService {

    public EventResponse createEvent(CreateEventRequest request);

    public EventAttendanceResponse scanEventAttendance(EventAttendanceScanRequest request);

    public EventResponse getEventById(Long eventId);

    public void deleteEventById(Long eventId);

    public EventResponse updateEvent(Long eventId, UpdateEventRequest updateEventRequest);

    public List<EventResponse> getAllEvents();

    public List<AttendanceListItemResponse> getEventAttendances(Long eventId);

    public EventAttendanceResponse scanEventAttendanceByQrCode(String qrCodeData);


}
