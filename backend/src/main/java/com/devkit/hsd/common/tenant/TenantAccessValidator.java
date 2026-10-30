package com.devkit.hsd.common.tenant;

import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.common.enums.SystemRole;
import com.devkit.hsd.entity.*;
import com.devkit.hsd.exception.ForbiddenException;
import org.springframework.stereotype.Component;

@Component
public class TenantAccessValidator {

    private final TenantResolver tenantResolver;

    public TenantAccessValidator(TenantResolver tenantResolver) {
        this.tenantResolver = tenantResolver;
    }


    public void validateAccess(Long resourceCommunityId){
        if (
                SystemRole.SUPER_ADMIN.equals(TenantContext.getSystemRole())
        ) {
            return;
        }
        Long userCommunityId = tenantResolver.resolveCommunityId();
        if (userCommunityId == null || !userCommunityId.equals(resourceCommunityId)) {
            throw new ForbiddenException(ErrorCode.ACCESS_DENIED, "Bu topluluğun kaynaklarına erişim yetkiniz yok!");
        }
    }

    public void validateUserInScope(User user) {
        if (user != null && user.getCommunity() != null) {
            validateAccess(user.getCommunity().getId());
        }
    }

    public void validateProjectInScope(Project project) {
        if (project != null && project.getCommunity() != null) {
            validateAccess(project.getCommunity().getId());
        }
    }

    public void validateCommitteeInScope(Committee committee) {
        if (committee != null && committee.getCommunity() != null) {
            validateAccess(committee.getCommunity().getId());
        }
    }

    public void validateEventInScope(Event event) {
        if (event != null && event.getCommunity() != null) {
            validateAccess(event.getCommunity().getId());
        }
    }

    public void validateMeetingInScope(Meeting meeting) {
        if (meeting != null && meeting.getCommunity() != null) {
            validateAccess(meeting.getCommunity().getId());
        }
    }

    public void validateProjectTaskInScope(ProjectTask projectTask) {
        if (projectTask != null && projectTask.getCommunity() != null) {
            validateAccess(projectTask.getCommunity().getId());
        }
    }
}
