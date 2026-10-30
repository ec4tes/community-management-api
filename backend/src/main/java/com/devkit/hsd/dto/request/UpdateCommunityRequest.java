package com.devkit.hsd.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommunityRequest {
    private String name;
    private String universityName;
    private String city;
    private String slug;
    private Boolean active;
}