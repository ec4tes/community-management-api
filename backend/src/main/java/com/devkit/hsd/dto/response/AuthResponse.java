package com.devkit.hsd.dto.response;


import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    public AuthResponse(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    private String access;

    private String refresh;

    private Long communityId;

    private SystemRole systemRole;

    private Role role;


}
