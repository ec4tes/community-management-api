package com.devkit.hsd.service.abs;

import com.devkit.hsd.dto.request.CreateProjectRequest;
import com.devkit.hsd.dto.response.ProjectBoardResponse;
import com.devkit.hsd.dto.response.ProjectResponse;
import com.devkit.hsd.dto.request.UpdateProjectRequest;

import java.util.List;

public interface IProjectService {

    public ProjectResponse createProject(CreateProjectRequest createProjectRequest);

    public List<ProjectResponse> getAllProjects();

    public ProjectResponse getProjectById(Long id);

    public ProjectBoardResponse getProjectBoard(Long id);

    public ProjectResponse updateProject(Long id, UpdateProjectRequest request);

    public void deleteProject(Long id);


}
