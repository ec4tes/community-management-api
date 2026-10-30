package com.devkit.hsd.mapper;

import com.devkit.hsd.dto.response.ProjectTaskBoardCardResponse;
import com.devkit.hsd.entity.ProjectTask;
import com.devkit.hsd.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectBoardMapper {

    @Mapping(source = "assignedTo.id", target = "assigneeId")
    @Mapping(source = "assignedTo", target = "assigneeFullName", qualifiedByName = "toAssigneeFullName")
    ProjectTaskBoardCardResponse toProjectTaskBoardCardResponse(ProjectTask projectTask);

    List<ProjectTaskBoardCardResponse> toProjectTaskBoardCardResponseList(List<ProjectTask> projectTasks);

    @Named("toAssigneeFullName")
    default String toAssigneeFullName(User user) {
        if (user == null) {
            return null;
        }
        return (user.getFirstName() + " " + user.getLastName()).trim();
    }
}