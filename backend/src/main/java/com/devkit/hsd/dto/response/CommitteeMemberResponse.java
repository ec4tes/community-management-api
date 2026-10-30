package com.devkit.hsd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommitteeMemberResponse extends BaseResponseDto{


    private String username;

    private String firstName;

    private String lastName;



}
