package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;
import com.devkit.hsd.dto.request.CommitteeRequest;
import com.devkit.hsd.dto.request.UpdateCommitteeRequest;
import com.devkit.hsd.dto.response.CommitteeMemberResponse;
import com.devkit.hsd.dto.response.CommitteeResponse;
import com.devkit.hsd.entity.Community;
import com.devkit.hsd.entity.Committee;
import com.devkit.hsd.entity.User;
import com.devkit.hsd.exception.BadRequestException;
import com.devkit.hsd.exception.ForbiddenException;
import com.devkit.hsd.exception.NotFoundException;
import com.devkit.hsd.mapper.CommitteeMapper;
import com.devkit.hsd.mapper.UserMapper;
import com.devkit.hsd.repository.CommitteeRepository;
import com.devkit.hsd.repository.CommunityRepository;
import com.devkit.hsd.repository.UserRepository;
import com.devkit.hsd.service.abs.IAuthService;
import com.devkit.hsd.service.abs.ICommitteeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommitteeServiceImpl implements ICommitteeService {

    private final CommitteeRepository committeeRepository;

    private final UserRepository userRepository;

    private final CommunityRepository communityRepository;

    private final CommitteeMapper committeeMapper;

    private final UserMapper userMapper;

    private final IAuthService authService;

    public CommitteeServiceImpl(CommitteeRepository committeeRepository, UserRepository userRepository, CommunityRepository communityRepository, CommitteeMapper committeeMapper, UserMapper userMapper, IAuthService authService) {
        this.committeeRepository = committeeRepository;
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.committeeMapper = committeeMapper;
        this.userMapper = userMapper;
        this.authService = authService;
    }

    private User getAuthorizedCommitteeUser() {
    User user = authService.getAuthenticatedUser();

    if (user.getSystemRole() == SystemRole.SUPER_ADMIN || user.getSystemRole() == SystemRole.COMMUNITY_ADMIN) {
        return user;
    }

    if (!(user.getRole().equals(Role.BASKAN)
            || user.getRole().equals(Role.BASKAN_YARDIMCISI)
            || user.getRole().equals(Role.KOMITE_LIDERI)
            || user.getRole().equals(Role.KOMITE_YARDIMCISI))) {

        throw new ForbiddenException(
                ErrorCode.ACCESS_DENIED,
                "Bu işlem için yetkiniz bulunmamaktadır."
        );
    }

    return user;
}


    private User findUserById(Long id, Long communityId){
        return userRepository.findByIdAndCommunityId(id, communityId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Bu id`ye sahip bir kullanıcı bulunmamaktadır."));
    }

    private List<User> findCommitteeMembers(List<Long> memberIds, Long communityId){
        List<User> members = userRepository.findAllByIdInAndCommunityId(memberIds, communityId);
        if (members.size() != memberIds.size()){
            throw new BadRequestException(ErrorCode.USER_NOT_FOUND, "Üye listesinde geçersiz kullanıcı ID`leri bulunmaktadır.");
        }

        return members;
    }

    private Community resolveTargetCommunity(User user, Long communityId) {
        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return communityRepository.findById(communityId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.COMMUNITY_NOT_FOUND, "Böyle bir topluluk bulunamadı!"));
        }

        if (user.getCommunity() == null || !user.getCommunity().getId().equals(communityId)) {
            throw new ForbiddenException(ErrorCode.ACCESS_DENIED, "Sadece kendi topluluğunuz icin komite islemi yapabilirsiniz.");
        }

        return user.getCommunity();
    }

    @Override
    public CommitteeResponse createCommittee(CommitteeRequest committeeRequest) {
        User user = getAuthorizedCommitteeUser();
        Community targetCommunity = resolveTargetCommunity(user, committeeRequest.getCommunityId());

        Committee newCommittee = buildNewCommittee(new Committee(), committeeRequest, targetCommunity);

        Committee savedCommittee = committeeRepository.save(newCommittee);

        return committeeMapper.toCommitteeResponse(savedCommittee);

    }

    private Committee buildNewCommittee(Committee committee, CommitteeRequest committeeRequest, Community community){
        committee.setName(committeeRequest.getName());
        committee.setDescription(committeeRequest.getDescription());
        committee.setCommunity(community);

        committee.setLeader(findUserById(committeeRequest.getLeaderId(), community.getId()));
        committee.setViceLeader(findUserById(committeeRequest.getViceLeaderId(), community.getId()));
        committee.setMembers(findCommitteeMembers(committeeRequest.getMembersIds(), community.getId()));
        return committee;
        
    }

    @Override
    public List<CommitteeResponse> getAllCommittees() {

        User user = authService.getAuthenticatedUser();

        List<Committee> committeeRepositoryAll;
        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            committeeRepositoryAll = committeeRepository.findAll();
        } else {
            committeeRepositoryAll = committeeRepository.findByCommunityId(user.getCommunity().getId());
        }
        return committeeRepositoryAll.stream()
                .map(committeeMapper::toCommitteeResponse)
                .toList();

    }


    private  List<User> findCommitteeMembersByCommitteeId(Long id){
        Committee committeeByIdOrTwrow = findCommitteeByIdOrThrow(id);
        return committeeByIdOrTwrow.getMembers();
    }

    @Override
    public List<CommitteeMemberResponse> getCommitteeMembers(Long id) {

        List<User> repositoryById = findCommitteeMembersByCommitteeId(id);
        return repositoryById.stream().
                map(userMapper::toCommitteeMemberResponse).
                toList();


    }


    private Committee findCommitteeByIdOrThrow(Long id){

        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return committeeRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.COMMITTEE_NOT_FOUND, "Bu id`ye sahip bir komite bulunmamaktadır."));
        }

        return committeeRepository.findByIdAndCommunityId(id, user.getCommunity().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMITTEE_NOT_FOUND, "Bu id`ye sahip bir komite bulunmamaktadır."));
    }


    private void updateCommitteeFields(Committee committee, UpdateCommitteeRequest request){
        Long communityId = committee.getCommunity().getId();

        if (request.getName() != null){
            committee.setName(request.getName());
        }
        if (request.getDescription() != null){
            committee.setDescription(request.getDescription());
        }
        if (request.getLeaderId() != null){
            committee.setLeader(findUserById(request.getLeaderId(), communityId));
        }
        if (request.getViceLeaderId() != null){
            committee.setViceLeader(findUserById(request.getViceLeaderId(), communityId));
        }
        if (request.getMembersIds() != null){
            committee.setMembers(findCommitteeMembers(request.getMembersIds(), communityId));
        }
    }


    @Override
    public CommitteeResponse updateCommittee(Long id, UpdateCommitteeRequest updateCommitteeRequest) {

        getAuthorizedCommitteeUser();

        Committee committee = findCommitteeByIdOrThrow(id);

        updateCommitteeFields(committee, updateCommitteeRequest);

        Committee savedCommittee = committeeRepository.save(committee);

        return committeeMapper.toCommitteeResponse(savedCommittee);
        
    }

    
    @Override
    public CommitteeResponse getCommitteeById(Long id) {

        Committee committee = findCommitteeByIdOrThrow(id);

        return committeeMapper.toCommitteeResponse(committee);
    }


    @Override
    public void deleteCommittee(Long id) {

        getAuthorizedCommitteeUser();

        Committee committee = findCommitteeByIdOrThrow(id);

        committeeRepository.delete(committee);
    }
}
