package com.devkit.hsd.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommitteeResponse extends BaseResponseDto{

    private String name;

    private String description;

    private String leaderName;

    private String viceLeaderName;

    private Long leader;

    private Long viceLeader;

    private List<Long> members;

    private Long communityId;

}
