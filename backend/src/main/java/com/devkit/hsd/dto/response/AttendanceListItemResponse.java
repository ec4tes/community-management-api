package com.devkit.hsd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListItemResponse {

    private Long id;
    private LocalDateTime scannedAt;
    private Integer pointsEarned;
    private Long userId;
    private String userName;
    private String userFullName;
    private String profileImage;
}
