package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;
import com.devkit.hsd.dto.request.CreateCommunityRequest;
import com.devkit.hsd.dto.request.UpdateCommunityRequest;
import com.devkit.hsd.dto.response.AdminCommunitySummaryResponse;
import com.devkit.hsd.dto.response.CommunityResponse;
import com.devkit.hsd.entity.Community;
import com.devkit.hsd.entity.User;
import com.devkit.hsd.exception.ConflictException;
import com.devkit.hsd.exception.ForbiddenException;
import com.devkit.hsd.exception.NotFoundException;
import com.devkit.hsd.mapper.CommunityMapper;
import com.devkit.hsd.repository.*;
import com.devkit.hsd.service.abs.IAuthService;
import com.devkit.hsd.service.abs.ICommunityService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommunityServiceImpl implements ICommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMapper communityMapper;
    private final IAuthService authService;
    private final UserRepository userRepository;
    private final CommitteeRepository committeeRepository;
    private final ProjectRepository projectRepository;
    private final ProjectTaskRepository taskRepository;
    private final EventRepository eventRepository;
    private final MeetingRepository meetingRepository;

    public CommunityServiceImpl(CommunityRepository communityRepository, CommunityMapper communityMapper, IAuthService authService, UserRepository userRepository, CommitteeRepository committeeRepository, ProjectRepository projectRepository, ProjectTaskRepository taskRepository, EventRepository eventRepository, MeetingRepository meetingRepository) {
        this.communityRepository = communityRepository;
        this.communityMapper = communityMapper;
        this.authService = authService;
        this.userRepository = userRepository;
        this.committeeRepository = committeeRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
        this.meetingRepository = meetingRepository;
    }

    private void checkAdminAuthority() {
        User user = authService.getAuthenticatedUser();
        if (user.getSystemRole() != SystemRole.SUPER_ADMIN) {
            throw new ForbiddenException(ErrorCode.ACCESS_DENIED, "Bu işlem için yetkiniz yok!");
        }
    }

    @Transactional
    @Override
    public CommunityResponse createCommunity(CreateCommunityRequest request) {
        checkAdminAuthority();

        if (communityRepository.existsBySlug(request.getSlug())) {
            throw new ConflictException(ErrorCode.VALIDATION_ERROR, "Bu slug değerine sahip bir topluluk zaten mevcut!");
        }


        Community community = communityMapper.toEntity(request);
        Community saved = communityRepository.save(community);

        if (request.getAdminUserId() != null) {
            User adminUser = userRepository.findById(request.getAdminUserId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.VALIDATION_ERROR, "Atanacak admin kullanıcısı bulunamadı!"));

            adminUser.setSystemRole(SystemRole.COMMUNITY_ADMIN);
            adminUser.setRole(Role.BASKAN);
            adminUser.setCommunity(saved);

            userRepository.save(adminUser);
        }
        return communityMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public CommunityResponse updateCommunity(Long id, UpdateCommunityRequest request) {
        checkAdminAuthority();
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VALIDATION_ERROR, "Topluluk bulunamadı!"));

        if (request.getSlug() != null && !request.getSlug().equals(community.getSlug())) {
            if (communityRepository.existsBySlug(request.getSlug())) {
                throw new ConflictException(ErrorCode.VALIDATION_ERROR, "Bu slug değerine sahip bir topluluk zaten mevcut!");
            }
        }

        communityMapper.updateEntityFromRequest(request, community);
        Community updated = communityRepository.save(community);
        return communityMapper.toResponse(updated);
    }

    @Override
    public CommunityResponse getCommunityById(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VALIDATION_ERROR, "Topluluk bulunamadı!"));
        return communityMapper.toResponse(community);
    }

    @Override
    public CommunityResponse getCommunityBySlug(String slug) {
        Community community = communityRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VALIDATION_ERROR, "Topluluk bulunamadı!"));
        return communityMapper.toResponse(community);
    }

    @Override
    public List<CommunityResponse> getAllCommunities() {
        checkAdminAuthority();
        List<Community> communities = communityRepository.findAll();
        return communityMapper.toResponseList(communities);
    }

    @Transactional
    @Override
    public void deleteCommunity(Long id) {
        checkAdminAuthority();
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VALIDATION_ERROR, "Topluluk bulunamadı!"));

        long totalUsers = userRepository.countByCommunityId(id);
        long totalCommittees = committeeRepository.countByCommunityId(id);
        long totalProjects = projectRepository.countByCommunityId(id);
        long totalTasks = taskRepository.countByCommunityId(id);
        long totalEvents = eventRepository.countByCommunityId(id);
        long totalMeetings = meetingRepository.countByCommunityId(id);

        if (totalUsers > 0
                || totalCommittees > 0
                || totalProjects > 0
                || totalTasks > 0
                || totalEvents > 0
                || totalMeetings > 0) {
            throw new ConflictException(
                    ErrorCode.VALIDATION_ERROR,
                    "Bağlı kullanıcı, komite, proje, görev, etkinlik veya toplantı bulunan topluluk silinemez!"
            );
        }

        communityRepository.delete(community);
    }

    @Transactional
    @Override
    public CommunityResponse changeCommunityStatus(Long id, boolean isActive) {

        checkAdminAuthority();

        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMUNITY_NOT_FOUND, "Böyle bir topluluk bulunamadı!"));

        community.setActive(isActive);
        Community savedCommunity = communityRepository.save(community);
        return communityMapper.toResponse(savedCommunity);
    }

    private AdminCommunitySummaryResponse buildCommunitySummary(Long id){

        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMUNITY_NOT_FOUND, "Böyle bir topluluk bulunamadı!"));
        AdminCommunitySummaryResponse response = communityMapper.toSummaryResponse(community);

        response.setTotalUsers(userRepository.countByCommunityId(id));
        response.setTotalCommittees(committeeRepository.countByCommunityId(id));
        response.setTotalProjects(projectRepository.countByCommunityId(id));
        response.setTotalTasks(taskRepository.countByCommunityId(id));
        response.setTotalEvents(eventRepository.countByCommunityId(id));
        response.setTotalMeetings(meetingRepository.countByCommunityId(id));
        return response;
    }

    @Override
    public AdminCommunitySummaryResponse getCommunitySummaryForAdmin(Long id) {

        checkAdminAuthority();
        return buildCommunitySummary(id);

    }
}
