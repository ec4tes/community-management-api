package com.devkit.hsd.repository;

import com.devkit.hsd.entity.Committee;
import com.devkit.hsd.entity.Project;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    boolean existsByNameAndCommittee(String name, Committee committee);

    List<Project> findByCommunityId(Long communityId);

    Optional<Project> findByIdAndCommunityId(Long id, Long communityId);

    Long countByCommunityId(Long communityId);

}
