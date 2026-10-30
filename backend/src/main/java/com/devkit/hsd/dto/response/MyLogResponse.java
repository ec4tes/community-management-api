package com.devkit.hsd.dto.response;

import com.devkit.hsd.common.enums.ActivityLogActionType;
import com.devkit.hsd.common.enums.ActivityLogSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyLogResponse extends BaseResponseDto{


    private ActivityLogActionType actionType;

    private ActivityLogSource source;

    private Long sourceId;

    private Integer points;

    private String description;

    private Long userId;

    private String username;

}
