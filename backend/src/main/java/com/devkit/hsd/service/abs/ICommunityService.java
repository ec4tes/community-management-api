package com.devkit.hsd.service.abs;

import com.devkit.hsd.dto.request.CreateCommunityRequest;
import com.devkit.hsd.dto.request.UpdateCommunityRequest;
import com.devkit.hsd.dto.response.AdminCommunitySummaryResponse;
import com.devkit.hsd.dto.response.CommunityResponse;
import java.util.List;

public interface ICommunityService {

    CommunityResponse createCommunity(CreateCommunityRequest request);

    CommunityResponse updateCommunity(Long id, UpdateCommunityRequest request);

    CommunityResponse getCommunityById(Long id);

    CommunityResponse getCommunityBySlug(String slug);

    List<CommunityResponse> getAllCommunities();

    void deleteCommunity(Long id);

    CommunityResponse changeCommunityStatus(Long id, boolean isActive);

    AdminCommunitySummaryResponse getCommunitySummaryForAdmin(Long id);


}