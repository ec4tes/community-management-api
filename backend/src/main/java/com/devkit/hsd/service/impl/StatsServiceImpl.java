package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.common.enums.ActivityLogSource;
import com.devkit.hsd.common.tenant.TenantResolver;
import com.devkit.hsd.dto.projection.CurrentMonthLeaderboardProjection;
import com.devkit.hsd.dto.request.MonthlyHistoryRequest;
import com.devkit.hsd.dto.response.LeaderBoardResponse;
import com.devkit.hsd.dto.response.MonthlyHistoryResponse;
import com.devkit.hsd.dto.response.MyLogResponse;
import com.devkit.hsd.dto.response.MyStatsResponse;
import com.devkit.hsd.dto.response.PointSourceSummaryResponse;
import com.devkit.hsd.entity.ActivityLog;
import com.devkit.hsd.entity.User;
import com.devkit.hsd.exception.BadRequestException;
import com.devkit.hsd.mapper.ActivityLogMapper;
import com.devkit.hsd.repository.ActivityLogRepository;
import com.devkit.hsd.repository.EventAttendanceRepository;
import com.devkit.hsd.repository.TaskPointRecordRepository;
import com.devkit.hsd.service.abs.IAuthService;
import com.devkit.hsd.service.abs.IStatsService;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements IStatsService {

    private final IAuthService authService;

    private final ActivityLogMapper activityLogMapper;

    private final TaskPointRecordRepository taskPointRecordRepository;

    private final ActivityLogRepository activityLogRepository;

    private final TenantResolver tenantResolver;

    private final EventAttendanceRepository eventAttendanceRepository;

    public StatsServiceImpl(IAuthService authService,
                            ActivityLogMapper activityLogMapper,
                            TaskPointRecordRepository taskPointRecordRepository,
                            ActivityLogRepository activityLogRepository,
                            TenantResolver tenantResolver,
                            EventAttendanceRepository eventAttendanceRepository) {
        this.authService = authService;
        this.activityLogMapper = activityLogMapper;
        this.taskPointRecordRepository = taskPointRecordRepository;
        this.activityLogRepository = activityLogRepository;
        this.tenantResolver = tenantResolver;
        this.eventAttendanceRepository = eventAttendanceRepository;
    }

    private Long getCompletedTaskCount(Long id){
        return taskPointRecordRepository.countByUserId(id);
    }

    private Integer getCurrentMonthPoints(Long userId, LocalDateTime startOfMonth, LocalDateTime endOfMonth){

        return taskPointRecordRepository.sumPointsByUserIdAndDateRange(userId, startOfMonth, endOfMonth);
    }

    private Long getCurrentMonthCompletedTaskCount(Long userId, LocalDateTime startOfMonth, LocalDateTime endOfMonth){

        return taskPointRecordRepository.countByUserIdAndDateRange(userId, startOfMonth, endOfMonth);
    }

    private Long getCompletedEventCount(Long userId) {
        return eventAttendanceRepository.countByUserId(userId);
    }

    private Long getCurrentMonthCompletedEventCount(Long userId, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
        return eventAttendanceRepository.countByUserIdAndScannedAtGreaterThanEqualAndScannedAtLessThan(userId, startOfMonth, endOfMonth);
    }

    private Integer calculateCurrentLevel(Integer totalPoints) {
        int safePoints = totalPoints != null ? totalPoints : 0;
        return (safePoints / 100) + 1;
    }

    private Integer calculatePointsToNextLevel(Integer totalPoints) {
        int safePoints = totalPoints != null ? totalPoints : 0;
        int remainder = safePoints % 100;
        return remainder == 0 ? 100 : 100 - remainder;
    }

    private List<PointSourceSummaryResponse> buildPointsBySource(List<ActivityLog> activityLogs, LocalDateTime startOfMonth) {
        Map<ActivityLogSource, Integer> totalsBySource = activityLogs.stream()
                .filter(log -> log.getCreatedAt() != null && !log.getCreatedAt().isBefore(startOfMonth))
                .filter(log -> log.getPoints() != null && log.getPoints() > 0)
                .collect(Collectors.groupingBy(
                        ActivityLog::getSource,
                        Collectors.summingInt(ActivityLog::getPoints)
                ));

        return totalsBySource.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Enum::name)))
                .map(entry -> new PointSourceSummaryResponse(entry.getKey().name(), entry.getValue()))
                .toList();
    }

    private List<MyLogResponse> buildRecentActivities(List<ActivityLog> activityLogs) {
        return activityLogs.stream()
                .limit(10)
                .map(activityLogMapper::toMyLogResponse)
                .toList();
    }

    private MyStatsResponse buildStatsResponse(
            Integer totalPoints,
            Long completedTaskCount,
            Long completedEventCount,
            Long totalActivityCount,
            Integer currentMonthPoints,
            Long currentMonthCompletedTaskCount,
            Long currentMonthCompletedEventCount,
            List<PointSourceSummaryResponse> pointsBySource,
            List<MyLogResponse> recentActivities
    ){

        MyStatsResponse myStatsResponse = new MyStatsResponse();
        myStatsResponse.setTotalPoints(totalPoints);
        myStatsResponse.setCompletedTaskCount(completedTaskCount);
        myStatsResponse.setCompletedEventCount(completedEventCount);
        myStatsResponse.setTotalActivityCount(totalActivityCount);
        myStatsResponse.setCurrentMonthCompletedTaskCount(currentMonthCompletedTaskCount);
        myStatsResponse.setCurrentMonthPoints(currentMonthPoints);
        myStatsResponse.setCurrentMonthCompletedEventCount(currentMonthCompletedEventCount);
        myStatsResponse.setCurrentLevel(calculateCurrentLevel(totalPoints));
        myStatsResponse.setPointsToNextLevel(calculatePointsToNextLevel(totalPoints));
        myStatsResponse.setPointsBySource(pointsBySource);
        myStatsResponse.setRecentActivities(recentActivities);

        return myStatsResponse;

    }

    @Override
    public MyStatsResponse getMyStats() {

        User authenticatedUser = authService.getAuthenticatedUser();
        Integer totalPoints = authenticatedUser.getTotalPoints();


        LocalDateTime startOfMonth = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

        Long completedTaskCount = getCompletedTaskCount(authenticatedUser.getId());
        Integer currentMonthPoints = getCurrentMonthPoints(authenticatedUser.getId(), startOfMonth, endOfMonth);
        Long currentMonthCompletedTaskCount = getCurrentMonthCompletedTaskCount(authenticatedUser.getId(), startOfMonth, endOfMonth);
        Long completedEventCount = getCompletedEventCount(authenticatedUser.getId());
        Long currentMonthCompletedEventCount = getCurrentMonthCompletedEventCount(authenticatedUser.getId(), startOfMonth, endOfMonth);
        List<ActivityLog> userActivityLogs = activityLogRepository.findAllByUserIdAndCommunityIdOrderByCreatedAtDesc(
                authenticatedUser.getId(),
                authenticatedUser.getCommunity().getId()
        );


        MyStatsResponse myStatsResponse = buildStatsResponse(
                totalPoints,
                completedTaskCount,
                completedEventCount,
                (long) userActivityLogs.size(),
                currentMonthPoints,
                currentMonthCompletedTaskCount,
                currentMonthCompletedEventCount,
                buildPointsBySource(userActivityLogs, startOfMonth),
                buildRecentActivities(userActivityLogs)
        );
        return myStatsResponse;

    }

    @Override
    public List<MyLogResponse> getMyLogs() {
        User authenticatedUser = authService.getAuthenticatedUser();
        List<ActivityLog> userActivityLogs = activityLogRepository.findAllByUserIdAndCommunityIdOrderByCreatedAtDesc(authenticatedUser.getId(), authenticatedUser.getCommunity().getId());

        return userActivityLogs.stream()
                .map(activityLogMapper::toMyLogResponse)
                .toList();

    }

    private MonthlyHistoryResponse buildMonthlyHistoryResponse(MonthlyHistoryRequest request,
                                                              Long completedTaskInOneMonth,
                                                              Long completedEventInOneMonth,
                                                              Integer totalPointsInOneMonth){

        MonthlyHistoryResponse monthlyHistoryResponse = new MonthlyHistoryResponse();
        monthlyHistoryResponse.setCompletedEventCount(completedEventInOneMonth);
        monthlyHistoryResponse.setCompletedTaskCount(completedTaskInOneMonth);
        monthlyHistoryResponse.setTotalPoints(totalPointsInOneMonth != null ? totalPointsInOneMonth : 0);
        monthlyHistoryResponse.setMonth(request.getYear() + "-" + String.format("%02d", request.getMonth()));

        return monthlyHistoryResponse;
    }

    @Override
    public MonthlyHistoryResponse getMonthlyHistory(MonthlyHistoryRequest request) {

        User authenticatedUser = authService.getAuthenticatedUser();
        Integer month = request.getMonth();
        Integer year = request.getYear();

        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        Integer totalPointsInOneMonth = taskPointRecordRepository.sumPointsByUserIdAndDateRange(authenticatedUser.getId(), startOfMonth, endOfMonth);
        Long completedTaskInOneMonth = taskPointRecordRepository.countByUserIdAndDateRange(authenticatedUser.getId(), startOfMonth, endOfMonth);
        Long completedEventInOneMonth = getCurrentMonthCompletedEventCount(authenticatedUser.getId(), startOfMonth, endOfMonth);

        return buildMonthlyHistoryResponse(request, completedTaskInOneMonth, completedEventInOneMonth, totalPointsInOneMonth);



    }

    @Override
    public List<LeaderBoardResponse> getCurrentMonthLeaderboard(Long communityId) {

        Long targetCommunityId = communityId;
        User authenticatedUser = authService.getAuthenticatedUser();

        if (!com.devkit.hsd.common.enums.SystemRole.SUPER_ADMIN.equals(authenticatedUser.getSystemRole())) {
            targetCommunityId = tenantResolver.resolveCommunityId();
        }

        if (targetCommunityId == null) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Topluluk belirtilmelidir!");
        }

        LocalDateTime startOfMonth = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        List<CurrentMonthLeaderboardProjection> currentMonthLeaderboard = taskPointRecordRepository.findCurrentMonthLeaderboard(startOfMonth, endOfMonth, targetCommunityId);

        return getLeaderBoardResponses(currentMonthLeaderboard);
    }

    private static @NonNull List<LeaderBoardResponse> getLeaderBoardResponses(List<CurrentMonthLeaderboardProjection> currentMonthLeaderboard) {
        List<LeaderBoardResponse> leaderBordResponse = new ArrayList<>();

        for (int i = 0; i < currentMonthLeaderboard.size(); i++){
            CurrentMonthLeaderboardProjection item = currentMonthLeaderboard.get(i);
            LeaderBoardResponse response = new LeaderBoardResponse();
            response.setUserId(item.getUserId());
            response.setUsername(item.getUsername());
            response.setTotalPoints(item.getTotalPoints());
            response.setRank(i + 1);
            leaderBordResponse.add(response);
        }
        return leaderBordResponse;
    }
}
