package com.devkit.hsd.dto.response;

import com.devkit.hsd.common.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectBoardColumnResponse {


    private TaskStatus status;
    private Long count;
    private List<ProjectTaskBoardCardResponse> tasks;
}
