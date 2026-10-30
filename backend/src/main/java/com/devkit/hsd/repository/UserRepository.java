package com.devkit.hsd.repository;

import com.devkit.hsd.dto.request.ChangePasswordRequest;
import com.devkit.hsd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

        Optional<User> findByUsername(String username);

        boolean existsByUsername(String username);

        boolean existsByEmail(String email);

        boolean existsByUsernameAndIdNot(String username, Long currentUserId);

        boolean existsByEmailAndIdNot(String email, Long currentUserId);

        Optional<User> findByIdAndCommunityId(Long id, Long communityId);

        List<User> findByCommunityId(Long communityId);

        List<User> findAllByIdInAndCommunityId(Collection<Long> id, Long community_id);

        boolean existsByUsernameAndCommunityId(String username,Long communityId);

        boolean existsByUsernameAndCommunityIdAndIdNot(String username,Long communityId,Long id);

        Long countByCommunityId(Long communityId);
}

