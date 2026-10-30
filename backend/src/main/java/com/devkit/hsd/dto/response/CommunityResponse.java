package com.devkit.hsd.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommunityResponse extends BaseResponseDto {

    private String name;
    private String universityName;
    private String city;
    private String slug;
    private Boolean active;
    private Long communityId;

}