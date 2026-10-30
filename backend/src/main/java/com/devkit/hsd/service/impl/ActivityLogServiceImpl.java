package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.ActivityLogActionType;
import com.devkit.hsd.common.enums.ActivityLogSource;
import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.entity.ActivityLog;
import com.devkit.hsd.entity.ProjectTask;
import com.devkit.hsd.exception.NotFoundException;
import com.devkit.hsd.repository.ActivityLogRepository;
import com.devkit.hsd.service.abs.IActivityLogService;
import com.devkit.hsd.service.abs.IAuthService;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogServiceImpl implements IActivityLogService {


    private final ActivityLogRepository activityLogRepository;


    public ActivityLogServiceImpl(IAuthService authService, ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }


    @Override
    public void logTaskCompleted(ProjectTask projectTask) {


        if (projectTask == null) {
            throw new NotFoundException(ErrorCode.TASK_NOT_FOUND,"Proje görevi boş olamaz!");
        }

        if (projectTask.getId() == null) {
            throw new NotFoundException(ErrorCode.TASK_NOT_FOUND,"Proje Görev Idsi boş olamaz!");
        }

        if (projectTask.getAssignedTo() == null) {
            throw new NotFoundException(ErrorCode.TASK_ASSIGNEE_NOT_FOUND,"Görev tamamlanırken assigne olmalı!");
        }

        ActivityLog activityLog = getActivityLog(projectTask);
        activityLogRepository.save(activityLog);
    }

    private static  ActivityLog getActivityLog(ProjectTask projectTask) {
        ActivityLog activityLog = new ActivityLog();

        activityLog.setActionType(ActivityLogActionType.TASK_COMPLETED);
        activityLog.setSource(ActivityLogSource.PROJECT_TASK);
        activityLog.setSourceId(projectTask.getId());
        activityLog.setPoints(projectTask.getPoints());
        activityLog.setDescription(projectTask.getTitle() + " görevi tamamlandı!");
        activityLog.setUser(projectTask.getAssignedTo());
        activityLog.setProject(projectTask.getProject());
        activityLog.setCommunity(projectTask.getCommunity());
        activityLog.setProjectTask(projectTask);
        return activityLog;
    }
}
