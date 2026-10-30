package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.ApprovalStatus;
import com.devkit.hsd.common.enums.Priority;
import com.devkit.hsd.common.enums.ProjectStatus;
import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.entity.Community;
import com.devkit.hsd.dto.request.CreateProjectRequest;
import com.devkit.hsd.dto.request.UpdateProjectRequest;
import com.devkit.hsd.dto.response.ProjectResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private IAuthService authService;

    @Mock
    private CommitteeRepository committeeRepository;

    @Mock
    private ProjectTaskRepository projectTaskRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectBoardMapper projectBoardMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User leaderUser;

    @BeforeEach
    void setUp() {
        leaderUser = new User();
        leaderUser.setId(10L);
        leaderUser.setRole(Role.KOMITE_LIDERI);
        Community community = new Community();
        community.setId(7L);
        leaderUser.setCommunity(community);
    }

    @Test
    void createProject_shouldCreateAndReturnResponse() {
        CreateProjectRequest request = new CreateProjectRequest("Website", "New website", Priority.YUKSEK, 1L);

        Committee committee = new Committee();
        committee.setId(1L);

        Project mapped = new Project();
        mapped.setName("Website");

        Project saved = new Project();
        saved.setId(55L);
        saved.setName("Website");
        saved.setCommittee(committee);
        saved.setOwner(leaderUser);

        ProjectResponse response = new ProjectResponse();
        response.setId(55L);
        response.setName("Website");

        when(authService.getAuthenticatedUser()).thenReturn(leaderUser);
        when(committeeRepository.findByIdAndCommunityId(1L, 7L)).thenReturn(Optional.of(committee));
        when(projectRepository.existsByNameAndCommittee("Website", committee)).thenReturn(false);
        when(projectMapper.toProject(request)).thenReturn(mapped);
        when(projectRepository.save(mapped)).thenReturn(saved);
        when(projectMapper.toProjectResponse(saved)).thenReturn(response);

        ProjectResponse result = projectService.createProject(request);

        assertEquals(55L, result.getId());
        verify(projectRepository).save(mapped);
    }

    @Test
    void getAllProjects_shouldReturnMappedList() {
        Project project = new Project();
        project.setId(1L);
        ProjectResponse response = new ProjectResponse();
        response.setId(1L);

        when(authService.getAuthenticatedUser()).thenReturn(leaderUser);
        when(projectRepository.findByCommunityId(7L)).thenReturn(List.of(project));
        when(projectMapper.toProjectResponseList(List.of(project))).thenReturn(List.of(response));

        List<ProjectResponse> result = projectService.getAllProjects();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getProjectById_whenProjectMissing_shouldThrowNotFound() {
        when(authService.getAuthenticatedUser()).thenReturn(leaderUser);
        when(projectRepository.findByIdAndCommunityId(99L, 7L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.getProjectById(99L));
    }

    @Test
    void updateProject_whenDuplicateNameInCommittee_shouldThrowConflict() {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("Duplicated");
        request.setDescription("desc");
        request.setPriority(Priority.ORTA);
        request.setCommitteeId(2L);

        Project project = new Project();
        project.setId(1L);
        project.setName("Old");
        Committee oldCommittee = new Committee();
        oldCommittee.setId(1L);
        project.setCommittee(oldCommittee);

        Committee newCommittee = new Committee();
        newCommittee.setId(2L);

        when(authService.getAuthenticatedUser()).thenReturn(leaderUser);
        when(projectRepository.findByIdAndCommunityId(1L, 7L)).thenReturn(Optional.of(project));
        when(committeeRepository.findByIdAndCommunityId(2L, 7L)).thenReturn(Optional.of(newCommittee));
        when(projectRepository.existsByNameAndCommittee("Duplicated", newCommittee)).thenReturn(true);

        assertThrows(ConflictException.class, () -> projectService.updateProject(1L, request));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void updateProject_shouldApplyChangesAndReturnResponse() {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("Updated Project");
        request.setDescription("updated desc");
        request.setPriority(Priority.KRITIK);
        request.setCommitteeId(2L);
        request.setStatus(ProjectStatus.AKTIF);
        request.setApprovalStatus(ApprovalStatus.APPROVED);
        request.setIsActivity(Boolean.FALSE);

        Project project = new Project();
        project.setId(1L);
        project.setName("Old Project");
        Committee oldCommittee = new Committee();
        oldCommittee.setId(1L);
        project.setCommittee(oldCommittee);

        Committee newCommittee = new Committee();
        newCommittee.setId(2L);

        ProjectResponse response = new ProjectResponse();
        response.setId(1L);
        response.setName("Updated Project");

        when(authService.getAuthenticatedUser()).thenReturn(leaderUser);
        when(projectRepository.findByIdAndCommunityId(1L, 7L)).thenReturn(Optional.of(project));
        when(committeeRepository.findByIdAndCommunityId(2L, 7L)).thenReturn(Optional.of(newCommittee));
        when(projectRepository.existsByNameAndCommittee("Updated Project", newCommittee)).thenReturn(false);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toProjectResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.updateProject(1L, request);

        assertEquals("Updated Project", result.getName());
        assertEquals("Updated Project", project.getName());
        assertEquals(newCommittee, project.getCommittee());
        assertEquals(ProjectStatus.AKTIF, project.getStatus());
        assertEquals(ApprovalStatus.APPROVED, project.getApprovalStatus());
        assertEquals(Boolean.FALSE, project.getIsActive());
    }

    @Test
    void deleteProject_whenUserNotAuthorized_shouldThrowForbidden() {
        User member = new User();
        member.setRole(Role.UYE);
        when(authService.getAuthenticatedUser()).thenReturn(member);

        assertThrows(ForbiddenException.class, () -> projectService.deleteProject(5L));
        verify(projectRepository, never()).findByIdAndCommunityId(any(), any());
    }

    @Test
    void deleteProject_whenProjectMissing_shouldThrowNotFound() {
        when(authService.getAuthenticatedUser()).thenReturn(leaderUser);
        when(projectRepository.findByIdAndCommunityId(5L, 7L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.deleteProject(5L));
    }

    @Test
    void deleteProject_shouldDeleteTasksThenProject() {
        Project project = new Project();
        project.setId(5L);
        Community community = new Community();
        community.setId(7L);
        project.setCommunity(community);

        ProjectTask task = new ProjectTask();
        task.setId(90L);

        when(authService.getAuthenticatedUser()).thenReturn(leaderUser);
        when(projectRepository.findByIdAndCommunityId(5L, 7L)).thenReturn(Optional.of(project));
        when(projectTaskRepository.findByProjectIdAndCommunityId(5L, 7L)).thenReturn(List.of(task));

        projectService.deleteProject(5L);

        verify(projectTaskRepository).deleteAll(List.of(task));
        verify(projectRepository).delete(project);
    }
}
