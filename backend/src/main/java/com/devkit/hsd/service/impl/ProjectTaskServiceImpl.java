package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.*;
import com.devkit.hsd.dto.request.*;
import com.devkit.hsd.dto.response.ProjectTaskResponse;
import com.devkit.hsd.entity.*;
import com.devkit.hsd.exception.BadRequestException;
import com.devkit.hsd.exception.ConflictException;
import com.devkit.hsd.exception.ForbiddenException;
import com.devkit.hsd.exception.NotFoundException;
import com.devkit.hsd.mapper.ProjectTaskMapper;
import com.devkit.hsd.repository.*;
import com.devkit.hsd.service.abs.IActivityLogService;
import com.devkit.hsd.service.abs.IAuthService;
import com.devkit.hsd.service.abs.IProjectTaskService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskServiceImpl implements IProjectTaskService {

    private final ProjectTaskRepository projectTaskRepository;

    private final ProjectRepository projectRepository;

    private final ProjectTaskMapper projectTaskMapper;

    private final IAuthService authService;

    private final UserRepository userRepository;

    private final IActivityLogService activityLogService;

    private final TaskPointRecordRepository taskPointRecordRepository;

    public ProjectTaskServiceImpl(ProjectTaskRepository projectTaskRepository, ProjectRepository projectRepository, ProjectTaskMapper projectTaskMapper, IAuthService authService, UserRepository userRepository, ActivityLogRepository activityLogRepository, IActivityLogService activityLogService, TaskPointRecordRepository taskPointRecordRepository) {
        this.projectTaskRepository = projectTaskRepository;
        this.projectRepository = projectRepository;
        this.projectTaskMapper = projectTaskMapper;
        this.authService = authService;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
        this.taskPointRecordRepository = taskPointRecordRepository;
    }


    private User getAuthorizedCommitteeUser(){

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
    


    private Project getProjectIfExists(Long id){

        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN){
            return projectRepository.findById(id).
                    orElseThrow(() -> new NotFoundException(ErrorCode.PROJECT_NOT_FOUND, "Böyle bir proje bulunamadı!"));
        }

         return projectRepository.findByIdAndCommunityId(id,user.getCommunity().getId()).
                orElseThrow(() -> new NotFoundException(ErrorCode.PROJECT_NOT_FOUND, "Böyle bir proje bulunamadı!"));
    }

    private void checkIfProjectTaskAlreadyExists(CreateProjectTaskRequest createProjectTaskRequest,Project project){
        String projectTaskRequestName = createProjectTaskRequest.getTitle().trim();
        if (projectTaskRepository.existsByTitleAndProject(projectTaskRequestName, project)){
            throw new ConflictException(ErrorCode.TASK_ALREADY_EXISTS, "Bu proje icin ayni baslikta gorev zaten var!");
        }
    }

    private void validatePointNullOrZero(CreateProjectTaskRequest request){

            if (request.getPoints() == null){
                throw new BadRequestException(ErrorCode.TASK_POINT_NOT_EMPTY,"Görev puanı boş olamaz!");
            }

    }

    @Override
    public ProjectTaskResponse createProjectTask(CreateProjectTaskRequest createProjectTaskRequest) {

        User authenticatedUser = getAuthorizedCommitteeUser();
        Project project = getProjectIfExists(createProjectTaskRequest.getProjectId());
        checkIfProjectTaskAlreadyExists(createProjectTaskRequest,project);
        validatePointNullOrZero(createProjectTaskRequest);

        ProjectTask projectTask = projectTaskMapper.toProjectTask(createProjectTaskRequest);
        projectTask.setTitle(createProjectTaskRequest.getTitle().trim());
        projectTask.setDescription(createProjectTaskRequest.getDescription().trim());
        projectTask.setCreatedBy(authenticatedUser);
        projectTask.setPoints(createProjectTaskRequest.getPoints());

        projectTask.setCommunity(project.getCommunity());
        projectTask.setProject(project);
        projectTask.setCreatedBy(authenticatedUser);

        ProjectTask savedProjectTask = projectTaskRepository.save(projectTask);
        return projectTaskMapper.toProjectTaskResponse(savedProjectTask);

    }

    private ProjectTask getProjectTaskIfExists(Long id){

        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN){
            return projectTaskRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.TASK_NOT_FOUND,"Böyle bir görev bulunmaadı!"));
        }

        return projectTaskRepository.findByIdAndCommunityId(id,user.getCommunity().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.TASK_NOT_FOUND ,"Böyle bir görev bulunamadı!"));
    }

    @Override
    public List<ProjectTaskResponse> getAvailableTasks() {

        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return projectTaskRepository.findAll().stream()
                    .filter(task -> task.getAssignedTo() == null && task.getStatus() == TaskStatus.MUSAIT)
                    .map(projectTaskMapper::toProjectTaskResponse)
                    .toList();
        }

        List<ProjectTask> byAssignedToIsNullAndStatusAndCommunityId = projectTaskRepository.findByAssignedToIsNullAndStatusAndCommunityId(TaskStatus.MUSAIT,user.getCommunity().getId());
        return byAssignedToIsNullAndStatusAndCommunityId.stream()
                .map(projectTaskMapper::toProjectTaskResponse)
                .toList();

    }

    @Override
    public ProjectTaskResponse claimTask(Long id) {
        User authenticatedUser = authService.getAuthenticatedUser();

        ProjectTask projectTask = getProjectTaskIfExists(id);

        if (!(projectTask.getAssignedTo() == null && projectTask.getStatus() == TaskStatus.MUSAIT)){
            throw new ConflictException(ErrorCode.TASK_ASSIGNMENT_INVALID, "Bu görev zaten alınmış");
        }

        projectTask.setAssignedTo(authenticatedUser);
        projectTask.setStatus(TaskStatus.ALINDI);

        ProjectTask savedProjectTask = projectTaskRepository.save(projectTask);
        return projectTaskMapper.toProjectTaskResponse(savedProjectTask);

    }

    private void validateCompletableTask(ProjectTask projectTask,User authenticatedUser){


        if (projectTask.getStatus() == TaskStatus.TAMAMLANDI){
            throw new ConflictException(ErrorCode.TASK_ALREADY_COMPLETED, "Bu görev zaten tamamlanmış");
        }

        if (projectTask.getStatus() == TaskStatus.IPTAL_EDILDI){
            throw new ConflictException(ErrorCode.TASK_ALREADY_CANCELLED,"Bu görev iptal edilmiş");
        }

        if (projectTask.getStatus() == TaskStatus.MUSAIT){
            throw new ConflictException(ErrorCode.TASK_NOT_STARTED,"Bu göreve henüz başlanmamış");
        }

        if (projectTask.getAssignedTo() == null){
            throw new ConflictException(ErrorCode.TASK_ASSIGNEE_NOT_FOUND,"Bu görev kimseye atanmamış.");
        }

        if (!(projectTask.getAssignedTo().getId().equals(authenticatedUser.getId()))){
            throw new ConflictException(ErrorCode.TASK_ASSIGNMENT_INVALID,"Bu görev size ait değil!");
        }
    }

    @Override
    public List<ProjectTaskResponse> myTasks() {
        User authenticatedUser = authService.getAuthenticatedUser();
        List<ProjectTask> byAssignedTo = projectTaskRepository.findByAssignedToId(authenticatedUser.getId());

        return byAssignedTo.stream()
                .map(projectTaskMapper::toProjectTaskResponse)
                .toList();

    }

    @Override
    public ProjectTaskResponse completeTask(Long id) {


        User authenticatedUser = authService.getAuthenticatedUser();
        ProjectTask projectTask = getProjectTaskIfExists(id);

        validateCompletableTask(projectTask,authenticatedUser);

        projectTask.setStatus(TaskStatus.TAMAMLANDI);

        ProjectTask savedProjectTask = projectTaskRepository.save(projectTask);

        completeAndProcess(savedProjectTask);

        return projectTaskMapper.toProjectTaskResponse(savedProjectTask);


    }

    private void validateCancelableTask(ProjectTask projectTask){

        if (projectTask.getStatus() == TaskStatus.TAMAMLANDI){
            throw new ConflictException(ErrorCode.TASK_ALREADY_COMPLETED,"Bu görev zaten tamamlanmış");
        }

        if (projectTask.getStatus() == TaskStatus.IPTAL_EDILDI){
            throw new ConflictException(ErrorCode.TASK_ALREADY_CANCELLED,"Bu görev iptal edilmiş");
        }
    }


    @Override
    public ProjectTaskResponse cancelTask(Long id) {
        getAuthorizedCommitteeUser();
        ProjectTask projectTask = getProjectTaskIfExists(id);
        validateCancelableTask(projectTask);

        projectTask.setStatus(TaskStatus.IPTAL_EDILDI);
        ProjectTask savedProjectTask = projectTaskRepository.save(projectTask);

        return projectTaskMapper.toProjectTaskResponse(savedProjectTask);

    }

    private void validateTransferableTask(ProjectTask projectTask,User targetUser){

        if (projectTask.getStatus() == TaskStatus.TAMAMLANDI){
            throw new ConflictException(ErrorCode.TASK_ALREADY_COMPLETED,"Bu görev zaten tamamlanmış");
        }

        if (projectTask.getStatus() == TaskStatus.IPTAL_EDILDI){
            throw new ConflictException(ErrorCode.TASK_ALREADY_CANCELLED,"Bu görev iptal edilmiş");
        }

        if (projectTask.getStatus() == TaskStatus.MUSAIT){
            throw new ConflictException(ErrorCode.TASK_ASSIGNMENT_INVALID,"Bu görev henüz alınmamış.");
        }

        if (projectTask.getAssignedTo() == null){
            throw new ConflictException(ErrorCode.TASK_ASSIGNMENT_INVALID,"Bu görev kimseye atanmamış.");
        }

        if (projectTask.getAssignedTo().getId().equals(targetUser.getId())) {
            throw new ConflictException(ErrorCode.TASK_TRANSFER_INVALID,"Görev aynı kullanıcıya transfer edilemez!");
        }

    }


    @Override
    public ProjectTaskResponse transferTask(Long id, TransferProjectTaskRequest transferProjectTaskRequest) {


        User authorizedCommitteeUser = getAuthorizedCommitteeUser();
        ProjectTask projectTask = getProjectTaskIfExists(id);

        User targetUser;
        if (authorizedCommitteeUser.getSystemRole() == SystemRole.SUPER_ADMIN) {
            targetUser = userRepository.findById(transferProjectTaskRequest.getTargetUserId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Hedef kullanıcı kullanıcı bulunamadı"));
        } else {
            targetUser = userRepository.findByIdAndCommunityId(transferProjectTaskRequest.getTargetUserId(),authorizedCommitteeUser.getCommunity().getId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Hedef kullanıcı kullanıcı bulunamadı"));
        }

        validateTransferableTask(projectTask,targetUser);

        projectTask.setAssignedTo(targetUser);
        ProjectTask savedProjectTask = projectTaskRepository.save(projectTask);

        return projectTaskMapper.toProjectTaskResponse(savedProjectTask);
    }

    private void validateChangeableTaskStatus(ChangeProjectTaskStatusRequest request, ProjectTask projectTask){
        if (request.getStatus() == projectTask.getStatus()){
            throw  new ConflictException(ErrorCode.PROJECT_TASK_ALREADY_IN_THIS_STATUS,"Görev zaten bu durumda!");
        }
    }


    private void validatePointRecord(ProjectTask projectTask){
        if (taskPointRecordRepository.existsByProjectTask_Id(projectTask.getId())) {
            throw new ConflictException(
                    ErrorCode.TASK_POINT_RECORD_ALREADY_EXISTS,
                    "Bu görev için zaten puan kaydı var."
            );
        }

        if (projectTask.getAssignedTo() == null) {
            throw new ConflictException(
                    ErrorCode.TASK_ASSIGNEE_NOT_FOUND,
                    "Görevi alan kullanıcı bulunamadı!"
            );
        }

        if (projectTask.getPoints() == null || projectTask.getPoints() == 0) {
            throw new ConflictException(ErrorCode.TASK_POINT_RECORD_ALREADY_EXISTS,"Bu görevin puan kaydı zaten yapılmış.");
        }
    }

    private void createCompletedTaskPointRecord(ProjectTask projectTask){
        if (projectTask.getStatus() != TaskStatus.TAMAMLANDI) {
            return;
        }
        validatePointRecord(projectTask);

        User user = projectTask.getAssignedTo();

        TaskPointRecord taskPointRecord = new TaskPointRecord();
        taskPointRecord.setPoints(projectTask.getPoints());
        taskPointRecord.setDescription(projectTask.getTitle() + " görevi tamamlandı!");
        taskPointRecord.setUser(user);
        taskPointRecord.setProjectTask(projectTask);
        taskPointRecord.setProject(projectTask.getProject());
        taskPointRecord.setCommunity(projectTask.getCommunity());
        taskPointRecordRepository.save(taskPointRecord);

        Integer currentPoints = user.getTotalPoints() == null ? 0 : user.getTotalPoints();
        Integer earnedPoints = projectTask.getPoints() == null ? 0 : projectTask.getPoints();
        user.setTotalPoints(currentPoints + earnedPoints);

        userRepository.save(user);
    }

    private void completeAndProcess(ProjectTask projectTask){

        activityLogService.logTaskCompleted(projectTask);
        createCompletedTaskPointRecord(projectTask);

    }

    @Transactional
    @Override
    public ProjectTaskResponse changeTaskStatus(ChangeProjectTaskStatusRequest request, Long id) {
        ProjectTask projectTask = getProjectTaskIfExists(id);
        validateChangeableTaskStatus(request,projectTask);
        projectTask.setStatus(request.getStatus());
        ProjectTask savedProjectTask = projectTaskRepository.save(projectTask);

        completeAndProcess(savedProjectTask);


        return projectTaskMapper.toProjectTaskResponse(savedProjectTask);
    }

    private void validateTaskCanBeDeleted(ProjectTask projectTask){

        if (projectTask.getStatus() == TaskStatus.TAMAMLANDI){
            throw new ConflictException(ErrorCode.TASK_ALREADY_COMPLETED,"Bu görev tamamlanmış");
        }

        if (projectTask.getStatus() == TaskStatus.IPTAL_EDILDI){
            throw new ConflictException(ErrorCode.TASK_ALREADY_CANCELLED,"Bu görev iptal edilmiş");
        }

        if (projectTask.getAssignedTo() != null){
            throw new ConflictException(ErrorCode.TASK_ASSIGNMENT_INVALID,"Görev alınmış durumda silinemez!");
        }


    }


    @Override
    public void deleteTask(Long id) {
        ProjectTask projectTask = getProjectTaskIfExists(id);
        validateTaskCanBeDeleted(projectTask);
        projectTaskRepository.delete(projectTask);
    }

    private void validateUpdateTaskRole(User currentUser,ProjectTask projectTask){


        if (currentUser.getSystemRole().equals(SystemRole.COMMUNITY_ADMIN) || currentUser.getSystemRole().equals(SystemRole.SUPER_ADMIN)){
            return;
        }

        Committee committee = projectTask.getProject().getCommittee();
        boolean isCommitteeLeader = committee.getLeader() != null  && committee.getLeader().getId().equals(currentUser.getId());
        boolean isCommitteeViceLeader = committee.getViceLeader() != null && committee.getViceLeader().getId().equals(currentUser.getId());

        if (!isCommitteeLeader && !isCommitteeViceLeader) {
            throw new ForbiddenException(
                    ErrorCode.USER_NOT_AUTHORIZED_TO_UPDATE_TASK,
                    "Bu görevi güncelleme yetkiniz bulunmuyor."
            );

        }

    }

    private void validateUpdateTaskStatus(ProjectTask projectTask){

        if (projectTask.getStatus() == TaskStatus.TAMAMLANDI){
            throw new ConflictException(ErrorCode.TASK_ALREADY_COMPLETED,"Tamamlanmış görev güncellenemez!");
        }

        if (projectTask.getStatus() == TaskStatus.IPTAL_EDILDI){
            throw new ConflictException(ErrorCode.TASK_ALREADY_CANCELLED,"İptal edilmiş görevn güncellenemez!");
        }

    }

    private void validateUpdateProjectTaskRequest(UpdateProjectTaskRequest request) {

        boolean titleIsEmpty = request.getTitle() != null && request.getTitle().isBlank();
        boolean descriptionIsEmpty = request.getDescription() != null && request.getDescription().isBlank();
        boolean pointsIsNegative = request.getPoints() != null && request.getPoints() < 0;
        boolean pointsTooHigh = request.getPoints() != null && request.getPoints() > 1000;

        if (request.getTitle() == null
                && request.getDescription() == null
                && request.getPoints() == null) {
            throw new BadRequestException(
                    ErrorCode.INVALID_REQUEST,
                    "Güncelleme için en az bir alan gönderilmelidir."
            );
        }

        if (titleIsEmpty) {
            throw new BadRequestException( ErrorCode.VALIDATION_ERROR,"Görev başlığı boş olamaz.");
        }

        if (descriptionIsEmpty) {throw new BadRequestException(ErrorCode.VALIDATION_ERROR,"Görev açıklaması boş olamaz.");
        }

        if (pointsIsNegative) { throw new BadRequestException( ErrorCode.VALIDATION_ERROR, "Görev puanı negatif olamaz.");
        }

        if (pointsTooHigh) {  throw new BadRequestException( ErrorCode.VALIDATION_ERROR, "Görev puanı 1000 değerinden büyük olamaz.");
        }
    }

    private ProjectTask buildUpdateTaskRequest(ProjectTask projectTask, UpdateProjectTaskRequest request) {

        if (request.getTitle() != null) {
            projectTask.setTitle(request.getTitle().trim());
        }

        if (request.getDescription() != null) {
            projectTask.setDescription(request.getDescription().trim());
        }

        if (request.getPoints() != null) {
            projectTask.setPoints(request.getPoints());
        }

        return projectTaskRepository.save(projectTask);

    }


    @Override
    public ProjectTaskResponse updateProjectTask(Long id, UpdateProjectTaskRequest projectTaskRequest) {

        ProjectTask projectTaskIfExists = getProjectTaskIfExists(id);
        User authenticatedUser = authService.getAuthenticatedUser();

        validateUpdateTaskRole(authenticatedUser,projectTaskIfExists);
        validateUpdateTaskStatus(projectTaskIfExists);
        validateUpdateProjectTaskRequest(projectTaskRequest);
        ProjectTask savedProjectTask = buildUpdateTaskRequest(projectTaskIfExists, projectTaskRequest);

        return projectTaskMapper.toProjectTaskResponse(savedProjectTask);


    }
}
