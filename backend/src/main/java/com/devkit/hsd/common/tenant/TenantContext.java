package com.devkit.hsd.common.tenant;

import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;

public class TenantContext {


    private static final ThreadLocal<TenantInfo> CONTEXT = new ThreadLocal<>();

    public static void setTenantInfo(TenantInfo tenantInfo){
        CONTEXT.set(tenantInfo);
    }

    public static TenantInfo getTenantInfo(){
        return CONTEXT.get();
    }

    public static void clear(){
        CONTEXT.remove();
    }

    public static Long getCommunityId() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.getCommunityId() : null;
    }

    public static Long getUserId() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.getUserId() : null;
    }

    public static SystemRole getSystemRole() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.getSystemRole() : null;
    }

    public static Role getRole() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.getRole() : null;
    }

    public static class TenantInfo {
        private final Long userId;
        private final Long communityId;
        private final SystemRole systemRole;
        private final Role role;

        public TenantInfo(Long userId, Long communityId, SystemRole systemRole, Role role) {
            this.userId = userId;
            this.communityId = communityId;
            this.systemRole = systemRole;
            this.role = role;
        }

        public Long getUserId() {
            return userId;
        }

        public Role getRole() {
            return role;
        }

        public SystemRole getSystemRole() {
            return systemRole;
        }

        public Long getCommunityId() {
            return communityId;
        }


    }

}
