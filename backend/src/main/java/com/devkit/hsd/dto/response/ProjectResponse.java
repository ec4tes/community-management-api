package com.devkit.hsd.dto.response;

import com.devkit.hsd.common.enums.ApprovalStatus;
import com.devkit.hsd.common.enums.Priority;
import com.devkit.hsd.common.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse extends BaseResponseDto{

    private String name;

    private String description;

    private ProjectStatus status;

    private ApprovalStatus approvalStatus;

    private Priority priority;

    private boolean isActivity;

    private Long ownerId;

    private Long committeeId;

    private Long communityId;
}
