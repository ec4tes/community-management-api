package com.devkit.hsd.dto.response;

import com.devkit.hsd.common.enums.Priority;
import com.devkit.hsd.common.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTaskBoardCardResponse {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Long assigneeId;
    private String assigneeFullName;
    private LocalDate dueDate;
    private TaskStatus status;


}
