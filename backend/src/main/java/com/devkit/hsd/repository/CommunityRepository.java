package com.devkit.hsd.repository;

import com.devkit.hsd.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    boolean existsBySlug(String slug);

    Optional<Community> findBySlug(String slug);

}
