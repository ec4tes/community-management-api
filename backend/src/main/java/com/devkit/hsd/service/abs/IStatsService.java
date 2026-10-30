package com.devkit.hsd.service.abs;

import com.devkit.hsd.dto.request.MonthlyHistoryRequest;
import com.devkit.hsd.dto.response.LeaderBoardResponse;
import com.devkit.hsd.dto.response.MonthlyHistoryResponse;
import com.devkit.hsd.dto.response.MyLogResponse;
import com.devkit.hsd.dto.response.MyStatsResponse;

import java.util.List;

public interface IStatsService {

    public MyStatsResponse getMyStats();

    public List<MyLogResponse> getMyLogs();

    public MonthlyHistoryResponse getMonthlyHistory(MonthlyHistoryRequest request);

    public List<LeaderBoardResponse> getCurrentMonthLeaderboard(Long communityId);
}

