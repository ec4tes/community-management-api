package com.devkit.hsd.repository;

import com.devkit.hsd.dto.projection.CurrentMonthLeaderboardProjection;
import com.devkit.hsd.dto.response.LeaderBoardResponse;
import com.devkit.hsd.entity.TaskPointRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskPointRecordRepository extends JpaRepository<TaskPointRecord,Long> {

    boolean existsByProjectTask_Id(Long projectTaskId);

    @Query("""
    select coalesce(sum(t.points), 0)
    from TaskPointRecord t
    where t.user.id = :userId
      and t.createdAt >= :startOfMonth
      and t.createdAt < :endOfMonth
""")
    Integer sumPointsByUserIdAndDateRange(Long userId, LocalDateTime startOfMonth, LocalDateTime endOfMonth);


    @Query("""
    select count(t)
    from TaskPointRecord t
    where t.user.id = :userId
      and t.createdAt >= :startOfMonth
      and t.createdAt < :endOfMonth
""")
    Long countByUserIdAndDateRange(Long userId, LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    Long countByUserId(Long userId);


    List<TaskPointRecord> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
select
    t.user.id as userId,
    t.user.username as username,
    coalesce(sum(t.points), 0) as totalPoints
from TaskPointRecord t
where t.createdAt >= :startOfMonth
  and t.createdAt < :endOfMonth
  and t.community.id = :communityId 
group by t.user.id, t.user.username
order by coalesce(sum(t.points), 0) desc
""")
    List<CurrentMonthLeaderboardProjection> findCurrentMonthLeaderboard(
            LocalDateTime startOfMonth,
            LocalDateTime endOfMonth,
            Long communityId
    );
}
