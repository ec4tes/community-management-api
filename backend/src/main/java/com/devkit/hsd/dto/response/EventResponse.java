package com.devkit.hsd.dto.response;

import com.devkit.hsd.common.enums.ApprovalStatus;
import com.devkit.hsd.common.enums.EventTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EventResponse extends BaseResponseDto{

    private String title;
    private String description;
    private EventTypes eventType;
    private LocalDateTime dateTime;
    private String location;
    private String posterImage;
    private String qrCodeData;
    private LocalDateTime qrExpiresAt;
    private Integer attendancePoints;
    private ApprovalStatus approvalStatus;
    private Long communityId;

}