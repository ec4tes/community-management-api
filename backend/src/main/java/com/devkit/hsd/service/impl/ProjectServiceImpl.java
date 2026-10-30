package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;
import com.devkit.hsd.common.enums.TaskStatus;
import com.devkit.hsd.dto.request.CreateProjectRequest;
import com.devkit.hsd.dto.request.UpdateProjectRequest;
import com.devkit.hsd.dto.response.ProjectBoardColumnResponse;
import com.devkit.hsd.dto.response.ProjectBoardResponse;
import com.devkit.hsd.dto.response.ProjectResponse;
import com.devkit.hsd.dto.response.ProjectTaskBoardCardResponse;
import com.devkit.hsd.entity.Committee;
import com.devkit.hsd.entity.Project;
import com.devkit.hsd.entity.ProjectTask;
import com.devkit.hsd.entity.User;
import com.devkit.hsd.exception.ConflictException;
import com.devkit.hsd.exception.ForbiddenException;
import com.devkit.hsd.exception.NotFoundException;
import com.devkit.hsd.mapper.ProjectBoardMapper;
import com.devkit.hsd.mapper.ProjectMapper;
import com.devkit.hsd.repository.CommitteeRepository;
import com.devkit.hsd.repository.ProjectRepository;
import com.devkit.hsd.repository.ProjectTaskRepository;
import com.devkit.hsd.service.abs.IAuthService;
import com.devkit.hsd.service.abs.IProjectService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProjectServiceImpl implements IProjectService {


    private final ProjectRepository projectRepository;

    private final IAuthService authService;

    private final CommitteeRepository committeeRepository;

    private final ProjectTaskRepository projectTaskRepository;

    private final ProjectMapper projectMapper;

    private final ProjectBoardMapper projectBoardMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, IAuthService authService, CommitteeRepository committeeRepository, ProjectTaskRepository projectTaskRepository, ProjectMapper projectMapper, ProjectBoardMapper projectBoardMapper) {
        this.projectRepository = projectRepository;
        this.authService = authService;
        this.committeeRepository = committeeRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.projectMapper = projectMapper;
        this.projectBoardMapper = projectBoardMapper;
    }


    private User getAuthenticatedAndAuthorizedUser(){


        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN || user.getSystemRole() == SystemRole.COMMUNITY_ADMIN) {
            return user;
        }


        if (!((user.getRole().equals(Role.KOMITE_LIDERI))
                || (user.getRole().equals(Role.KOMITE_YARDIMCISI)))){
            throw new ForbiddenException(ErrorCode.ACCESS_DENIED, "Bu yetkiye sahip değilsiniz!");
        }

        return user;
    }


    private void checkIfProjectAlreadyExists(CreateProjectRequest createProjectRequest){
        String projectRequestName = createProjectRequest.getName().trim();
        Committee committeeIfExists = getCommitteeIfExists(createProjectRequest.getCommitteeId());
        if ( projectRepository.existsByNameAndCommittee(projectRequestName,committeeIfExists)){
            throw new ConflictException(ErrorCode.PROJECT_ALREADY_EXISTS, "Bu projeden zaten var!");
        }

    }

    private Committee getCommitteeIfExists(Long id){

        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return committeeRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.COMMITTEE_NOT_FOUND, "Böyle bir komite bulunmuyor"));
        }

        return committeeRepository.findByIdAndCommunityId(id, user.getCommunity().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMITTEE_NOT_FOUND, "Böyle bir komite bulunmuyor"));
    }

    @Override
    public ProjectResponse createProject(CreateProjectRequest createProjectRequest) {

        User authenticatedAndAuthorizedUser = getAuthenticatedAndAuthorizedUser();
        Committee committee = getCommitteeIfExists(createProjectRequest.getCommitteeId());

        checkIfProjectAlreadyExists(createProjectRequest);

        Project project = projectMapper.toProject(createProjectRequest);
        project.setOwner(authenticatedAndAuthorizedUser);
        project.setCommittee(committee);
        project.setCommunity(committee.getCommunity());

        Project savedProject = projectRepository.save(project);

        return projectMapper.toProjectResponse(savedProject);

    }

    @Override
    public List<ProjectResponse> getAllProjects() {

        User user = authService.getAuthenticatedUser();


        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return projectMapper.toProjectResponseList(projectRepository.findAll());
        }

        return projectMapper.toProjectResponseList(projectRepository.findByCommunityId(user.getCommunity().getId()));
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        return projectMapper.toProjectResponse(getProjectIfExists(id));
    }

    private Project getProjectIfExists(Long id){

        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return projectRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.PROJECT_NOT_FOUND, "Proje bulunamadı!"));
        }


        return projectRepository.findByIdAndCommunityId(id, user.getCommunity().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.PROJECT_NOT_FOUND, "Proje bulunamadı!"));
    }

    private List<ProjectTask> getProjectTaskByStatus(List<ProjectTask> projectTasks,TaskStatus taskStatus){
        return projectTasks.stream()
                .filter(task -> task.getStatus() == taskStatus).
                toList();
    }

    private List<ProjectTaskBoardCardResponse> getProjectTaskCardsByStatus(List<ProjectTask> projectTasks,TaskStatus taskStatus){

        List<ProjectTask> projectTaskByStatus = getProjectTaskByStatus(projectTasks, taskStatus);
        return projectBoardMapper.toProjectTaskBoardCardResponseList(projectTaskByStatus);

    }

    private ProjectBoardColumnResponse buildBoardColumn(
            List<ProjectTaskBoardCardResponse> projectTaskBoardCardResponseList,
            TaskStatus taskStatus
            ){
        ProjectBoardColumnResponse columnResponse = new ProjectBoardColumnResponse();
        columnResponse.setCount((long) projectTaskBoardCardResponseList.size());
        columnResponse.setStatus(taskStatus);
        columnResponse.setTasks(projectTaskBoardCardResponseList);

        return columnResponse;

    }

    @Override
    public ProjectBoardResponse getProjectBoard(Long id) {

        Project project = getProjectIfExists(id);
        List<ProjectTask> projectTasksById = projectTaskRepository.findByProjectIdAndCommunityId(project.getId(), project.getCommunity().getId());

        List<ProjectTaskBoardCardResponse> availableTasks = getProjectTaskCardsByStatus(projectTasksById, TaskStatus.MUSAIT);
        List<ProjectTaskBoardCardResponse> completedTasks = getProjectTaskCardsByStatus(projectTasksById, TaskStatus.TAMAMLANDI);
        List<ProjectTaskBoardCardResponse> inProgressTasks = getProjectTaskCardsByStatus(projectTasksById, TaskStatus.DEVAM_EDIYOR);
        List<ProjectTaskBoardCardResponse> takenTasks = getProjectTaskCardsByStatus(projectTasksById, TaskStatus.ALINDI);

        ProjectBoardColumnResponse availableColumn = buildBoardColumn(availableTasks, TaskStatus.MUSAIT);
        ProjectBoardColumnResponse takenColumn = buildBoardColumn(takenTasks, TaskStatus.ALINDI);
        ProjectBoardColumnResponse inProgressColumn = buildBoardColumn(inProgressTasks, TaskStatus.DEVAM_EDIYOR);
        ProjectBoardColumnResponse completedColumn = buildBoardColumn(completedTasks, TaskStatus.TAMAMLANDI);

        List<ProjectBoardColumnResponse> columns = List.of(
                availableColumn,
                takenColumn,
                inProgressColumn,
                completedColumn
        );

        return new ProjectBoardResponse(project.getId(),project.getName(),columns);
    }

    @Override
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        getAuthenticatedAndAuthorizedUser();

        Project project = getProjectIfExists(id);
        Committee committee = getCommitteeIfExists(request.getCommitteeId());

        validateProjectUpdateRules(project, request, committee);
        applyProjectUpdates(project, request, committee);

        Project updated = projectRepository.save(project);
        return projectMapper.toProjectResponse(updated);
    }

    private void validateProjectUpdateRules(Project project, UpdateProjectRequest request, Committee committee) {
        String newName = request.getName().trim();
        boolean nameChanged = !project.getName().equalsIgnoreCase(newName);
        boolean committeeChanged = !project.getCommittee().getId().equals(committee.getId());

        if ((nameChanged || committeeChanged) && projectRepository.existsByNameAndCommittee(newName, committee)) {
            throw new ConflictException(ErrorCode.PROJECT_ALREADY_EXISTS, "Bu projeden zaten var!");
        }
    }

    private void applyProjectUpdates(Project project, UpdateProjectRequest request, Committee committee) {
        project.setName(request.getName().trim());
        project.setDescription(request.getDescription().trim());
        project.setPriority(request.getPriority());
        project.setCommittee(committee);
        project.setCommunity(committee.getCommunity());

        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        if (request.getApprovalStatus() != null) {
            project.setApprovalStatus(request.getApprovalStatus());
        }
        if (request.getIsActivity() != null) {
            project.setIsActive(request.getIsActivity());
        }
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        getAuthenticatedAndAuthorizedUser();
        Project project = getProjectIfExists(id);
        List<ProjectTask> projectTasks = projectTaskRepository.findByProjectIdAndCommunityId(project.getId(), project.getCommunity().getId());
        if (!projectTasks.isEmpty()) {
            projectTaskRepository.deleteAll(projectTasks);
        }
        projectRepository.delete(project);
    }


}
