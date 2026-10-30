package com.devkit.hsd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyStatsResponse{

        private Integer totalPoints;
        private Long completedTaskCount;
        private Long completedEventCount;
        private Long totalActivityCount;
        private Integer currentMonthPoints;
        private Long currentMonthCompletedTaskCount;
        private Long currentMonthCompletedEventCount;
        private Integer currentLevel;
        private Integer pointsToNextLevel;
        private List<PointSourceSummaryResponse> pointsBySource;
        private List<MyLogResponse> recentActivities;
}
