package com.devkit.hsd.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProjectTaskRequest {

    private String title;

    private String description;

    private Integer points;
}