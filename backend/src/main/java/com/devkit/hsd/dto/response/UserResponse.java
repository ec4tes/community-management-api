package com.devkit.hsd.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.devkit.hsd.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse extends BaseResponseDto{


    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String profileImage;

    private String department;

    private String grade;

    private Role role;

    private Boolean isActive;

    private Boolean isOnline;

    private LocalDateTime lastSeen;

    private Long communityId;
}
