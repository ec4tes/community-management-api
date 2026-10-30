package com.devkit.hsd.service.abs;

import com.devkit.hsd.dto.request.*;
import com.devkit.hsd.dto.response.ProjectBoardResponse;
import com.devkit.hsd.dto.response.ProjectTaskResponse;
import com.devkit.hsd.entity.ProjectTask;
import com.devkit.hsd.entity.User;

import java.util.List;

public interface IProjectTaskService {

    public ProjectTaskResponse createProjectTask(CreateProjectTaskRequest createProjectTaskRequest);

    public List<ProjectTaskResponse> getAvailableTasks();

    public ProjectTaskResponse claimTask(Long id);

    public List<ProjectTaskResponse>  myTasks();

    public ProjectTaskResponse completeTask(Long id);

    public ProjectTaskResponse cancelTask(Long id);

    public ProjectTaskResponse transferTask(Long id, TransferProjectTaskRequest transferProjectTaskRequest);

    public ProjectTaskResponse changeTaskStatus(ChangeProjectTaskStatusRequest request, Long id);

    public void deleteTask(Long id);

    public ProjectTaskResponse updateProjectTask(Long id, UpdateProjectTaskRequest projectTaskRequest);

}
