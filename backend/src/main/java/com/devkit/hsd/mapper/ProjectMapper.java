package com.devkit.hsd.mapper;

import com.devkit.hsd.dto.request.CreateProjectRequest;
import com.devkit.hsd.dto.response.ProjectResponse;
import com.devkit.hsd.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "committee", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "approvalStatus", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "teamMembers", ignore = true)
    Project toProject(CreateProjectRequest createProjectRequest);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "committee.id", target = "committeeId")
    @Mapping(target = "communityId", source = "community.id")
    @Mapping(target = "activity", source = "isActive")
    ProjectResponse toProjectResponse(Project project);

    List<ProjectResponse> toProjectResponseList(List<Project> projects);
}
