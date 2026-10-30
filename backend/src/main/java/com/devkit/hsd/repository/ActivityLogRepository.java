package com.devkit.hsd.repository;

import com.devkit.hsd.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {

    List<ActivityLog> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<ActivityLog> findAllByUserIdAndCommunityIdOrderByCreatedAtDesc(Long userId, Long communityId);

    List<ActivityLog> findAllByCommunityIdOrderByCreatedAtDesc(Long communityId);
}
