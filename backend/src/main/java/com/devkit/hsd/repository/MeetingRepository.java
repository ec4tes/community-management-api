package com.devkit.hsd.repository;

import com.devkit.hsd.entity.Meeting;
import com.devkit.hsd.entity.MeetingAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting,Long> {

     boolean existsByTitleAndDateTimeAndLocationAndCommunityId(String title, LocalDateTime dateTime, String location, Long communityId);

     boolean existsByTitleAndDateTimeAndLocationAndCommunityIdAndIdNot(String title, LocalDateTime dateTime, String location, Long communityId, Long id);

     Optional<Meeting> findByIdAndCommunityId(Long id, Long communityId);

     List<Meeting> findByCommunityId(Long communityId);

     Long countByCommunityId(Long communityId);

}
