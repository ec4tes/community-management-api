package com.devkit.hsd.repository;

import com.devkit.hsd.entity.Committee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeRepository extends JpaRepository<Committee,Long> {

    List<Committee> findByCommunityId(Long communityId);

    Optional<Committee> findByIdAndCommunityId(Long id, Long communityId);

    Long countByCommunityId(Long communityId);

}
