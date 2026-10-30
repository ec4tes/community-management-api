package com.devkit.hsd.common.tenant;

import com.devkit.hsd.entity.User;
import com.devkit.hsd.service.abs.IAuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TenantResolver {


    public Long resolveCommunityId(){

        Long communityId = TenantContext.getCommunityId();
        if (communityId != null){
            return communityId;
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                return user.getCommunity() != null ? user.getCommunity().getId() : null;
            }
        }
        return null;
    }

    public Long resolveUserId() {
        Long userId = TenantContext.getUserId();
        if (userId != null) {
            return userId;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                return user.getId();
            }
        }
        return null;
    }
}
