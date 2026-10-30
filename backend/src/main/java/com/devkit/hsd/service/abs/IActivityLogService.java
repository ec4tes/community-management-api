package com.devkit.hsd.service.abs;

import com.devkit.hsd.entity.ProjectTask;

public interface IActivityLogService {

    void logTaskCompleted(ProjectTask projectTask);

}
