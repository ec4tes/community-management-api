package com.devkit.hsd.mapper;

import com.devkit.hsd.dto.response.CommitteeResponse;
import com.devkit.hsd.entity.Committee;
import com.devkit.hsd.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommitteeMapper {

    @Mapping(target = "leaderName", source = "leader")
    @Mapping(target = "viceLeaderName", source = "viceLeader")
    @Mapping(target = "leader", source = "leader.id")
    @Mapping(target = "viceLeader", source = "viceLeader.id")
    @Mapping(target = "members", source = "members")
    @Mapping(target = "communityId", source = "community.id")
    CommitteeResponse toCommitteeResponse(Committee committee);

    default String map(User user) {
        if (user == null) {
            return null;
        }
        return user.getFirstName() + " " + user.getLastName();
    }

    default List<Long> map(List<User> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream()
                .map(User::getId)
                .toList();
    }
}
