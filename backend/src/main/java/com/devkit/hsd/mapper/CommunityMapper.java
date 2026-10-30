package com.devkit.hsd.mapper;

import com.devkit.hsd.dto.request.CreateCommunityRequest;
import com.devkit.hsd.dto.request.UpdateCommunityRequest;
import com.devkit.hsd.dto.response.AdminCommunitySummaryResponse;
import com.devkit.hsd.dto.response.CommunityResponse;
import com.devkit.hsd.entity.Community;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommunityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Community toEntity(CreateCommunityRequest request);

    @Mapping(target = "communityId", source = "id")
    CommunityResponse toResponse(Community community);

    List<CommunityResponse> toResponseList(List<Community> communities);

    @Mapping(target = "communityId", source = "id")
    AdminCommunitySummaryResponse toSummaryResponse(Community community);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateCommunityRequest request, @MappingTarget Community community);
}
