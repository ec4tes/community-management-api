package com.devkit.hsd.mapper;

import com.devkit.hsd.dto.request.CreateProjectTaskRequest;
import com.devkit.hsd.dto.response.ProjectTaskResponse;
import com.devkit.hsd.entity.ProjectTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectTaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "points", ignore = true)
    ProjectTask toProjectTask(CreateProjectTaskRequest createProjectTaskRequest);

    @Mapping(source = "createdBy.username", target = "createdByUsername")
    @Mapping(source = "assignedTo.username", target = "assignedToUsername")
    @Mapping(target = "communityId", source = "community.id")
    ProjectTaskResponse toProjectTaskResponse(ProjectTask projectTask);
}