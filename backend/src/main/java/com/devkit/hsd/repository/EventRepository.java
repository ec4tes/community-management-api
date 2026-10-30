package com.devkit.hsd.repository;

import com.devkit.hsd.entity.Event;
import com.devkit.hsd.entity.EventAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {

    boolean existsByTitleAndDateTimeAndLocationAndCommunityId(String title, LocalDateTime dateTime, String location, Long communityId);

    boolean existsByTitleAndDateTimeAndLocationAndCommunityIdAndIdNot(String title, LocalDateTime dateTime, String location, Long communityId, Long id);

    List<Event> findByCommunityId(Long communityId);

    Optional<Event> findByIdAndCommunityId(Long id, Long communityId);

    Optional<Event> findByQrCodeDataAndCommunityId(String qrCodeData, Long communityId);

    Long countByCommunityId(Long communityId);


}
