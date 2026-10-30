package com.devkit.hsd.repository;

import com.devkit.hsd.entity.EventAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface EventAttendanceRepository extends JpaRepository<EventAttendance,Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    List<EventAttendance> findAllByEventId(Long eventId);

    boolean existsByEventIdAndUserIdAndCommunityId(Long eventId,Long userId,Long communityId);

    List<EventAttendance> findAllByEventIdAndCommunityId(Long eventId,Long communityId);

    Long countByUserId(Long userId);

    Long countByUserIdAndScannedAtGreaterThanEqualAndScannedAtLessThan(Long userId, LocalDateTime startOfMonth, LocalDateTime endOfMonth);


}
