package com.devkit.hsd.repository;

import com.devkit.hsd.entity.MeetingAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingAttendanceRepository extends JpaRepository<MeetingAttendance,Long> {

    boolean existsByMeetingIdAndUserId(Long meetingId, Long userId);

    List<MeetingAttendance> findAllByMeetingId(Long meetingId);

    boolean existsByMeetingIdAndUserIdAndCommunityId(Long meetingId,Long userId,Long communityId);

    List<MeetingAttendance> findAllByMeetingIdAndCommunityId(Long meetingId,Long communityId);

}
