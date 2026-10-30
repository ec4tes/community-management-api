package com.devkit.hsd.repository;

import com.devkit.hsd.common.enums.TaskStatus;
import com.devkit.hsd.entity.Project;
import com.devkit.hsd.entity.ProjectTask;
import com.devkit.hsd.entity.Task;
import com.devkit.hsd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask,Long> {

    boolean existsByTitleAndProject(String title, Project project);

    List<ProjectTask> findByAssignedToId(Long assignedToId);

    List<ProjectTask> findByProject_Id(Long projectId);

    List<ProjectTask> findByAssignedToIsNullAndStatusAndCommunityId(TaskStatus status, Long communityId);

    Optional<ProjectTask> findByIdAndCommunityId(Long id, Long communityId);

    List<ProjectTask> findByProjectIdAndCommunityId(Long projectId,Long communityId);

    Long countByCommunityId(Long communityId);
}
