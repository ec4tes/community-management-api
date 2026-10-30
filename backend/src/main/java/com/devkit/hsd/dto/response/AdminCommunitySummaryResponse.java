package com.devkit.hsd.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCommunitySummaryResponse extends CommunityResponse {

    private long totalUsers;

    private long totalCommittees;

    private long totalProjects;

    private long totalTasks;

    private long totalEvents;

    private long totalMeetings;

}
