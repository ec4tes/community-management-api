package com.devkit.hsd.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectBoardResponse {

    private Long projectId;
    private String projectName;
    private List<ProjectBoardColumnResponse> columns;


}
