package com.devkit.hsd.dto.request;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommitteeRequest {


    private String name;


    private String description;


    private Long leaderId;


    private Long viceLeaderId;


    private List<Long> membersIds;

}
