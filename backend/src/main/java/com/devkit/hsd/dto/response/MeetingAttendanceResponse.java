package com.devkit.hsd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MeetingAttendanceResponse {

    private Long meetingId;
    private String meetingTitle;
    private LocalDateTime scannedAt;
    private Integer pointsEarned;
    private String message;
}