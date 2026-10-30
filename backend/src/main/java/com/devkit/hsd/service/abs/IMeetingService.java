package com.devkit.hsd.service.abs;

import java.util.List;

import com.devkit.hsd.dto.request.CreateMeetingRequest;
import com.devkit.hsd.dto.request.MeetingAttendanceScanRequest;
import com.devkit.hsd.dto.request.UpdateMeetingRequest;
import com.devkit.hsd.dto.response.AttendanceListItemResponse;
import com.devkit.hsd.dto.response.MeetingAttendanceResponse;
import com.devkit.hsd.dto.response.MeetingResponse;

public interface IMeetingService {

    public MeetingResponse createMeeting(CreateMeetingRequest request);

    public MeetingAttendanceResponse scanMeetingAttendance(MeetingAttendanceScanRequest request);

    public MeetingResponse getMeetingById(Long meetingId);

    public MeetingResponse updateMeeting(Long meetingId, UpdateMeetingRequest request);

    public void deleteMeeting(Long meetingId);

    public List<MeetingResponse> getAllMeetings();

    public List<AttendanceListItemResponse> getMeetingAttendances(Long meetingId);
}
