package com.devkit.hsd.dto.response;

import com.devkit.hsd.common.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTaskResponse extends BaseResponseDto{

    private String title;

    private String description;

    private TaskStatus status;

    private String createdByUsername;

    private String assignedToUsername;

    private Long communityId;
}
