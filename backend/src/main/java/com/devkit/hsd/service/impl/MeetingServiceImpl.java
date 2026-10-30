package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;
import com.devkit.hsd.dto.request.CreateMeetingRequest;
import com.devkit.hsd.dto.request.MeetingAttendanceScanRequest;
import com.devkit.hsd.dto.request.UpdateMeetingRequest;
import com.devkit.hsd.dto.response.AttendanceListItemResponse;
import com.devkit.hsd.dto.response.MeetingAttendanceResponse;
import com.devkit.hsd.dto.response.MeetingResponse;
import com.devkit.hsd.entity.*;
import com.devkit.hsd.exception.BadRequestException;
import com.devkit.hsd.exception.ConflictException;
import com.devkit.hsd.exception.ForbiddenException;
import com.devkit.hsd.exception.NotFoundException;
import com.devkit.hsd.mapper.MeetingAttendanceMapper;
import com.devkit.hsd.mapper.MeetingMapper;
import com.devkit.hsd.repository.CommitteeRepository;
import com.devkit.hsd.repository.MeetingAttendanceRepository;
import com.devkit.hsd.repository.MeetingRepository;
import com.devkit.hsd.repository.UserRepository;
import com.devkit.hsd.service.abs.IAuthService;
import com.devkit.hsd.service.abs.IMeetingService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class MeetingServiceImpl implements IMeetingService {

    private final MeetingRepository meetingRepository;

    private final MeetingMapper meetingMapper;

    private final IAuthService authService;

    private final CommitteeRepository committeeRepository;

    private final MeetingAttendanceRepository meetingAttendanceRepository;

    private final MeetingAttendanceMapper meetingAttendanceMapper;

    private final UserRepository userRepository;

    public MeetingServiceImpl(MeetingRepository meetingRepository, MeetingMapper meetingMapper, IAuthService authService, CommitteeRepository committeeRepository, MeetingAttendanceRepository meetingAttendanceRepository, MeetingAttendanceMapper meetingAttendanceMapper, UserRepository userRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingMapper = meetingMapper;
        this.authService = authService;
        this.committeeRepository = committeeRepository;
        this.meetingAttendanceRepository = meetingAttendanceRepository;
        this.meetingAttendanceMapper = meetingAttendanceMapper;
        this.userRepository = userRepository;
    }

    private void checkMeetingAuthority(){
        User authenticatedUser = authService.getAuthenticatedUser();

        if (authenticatedUser.getSystemRole() == SystemRole.SUPER_ADMIN
                || authenticatedUser.getSystemRole() == SystemRole.COMMUNITY_ADMIN) {
            return;
        }

        if (!(authenticatedUser.getRole() == Role.KOMITE_LIDERI || authenticatedUser.getRole() == Role.KOMITE_YARDIMCISI)){
            throw new ForbiddenException(ErrorCode.USER_NOT_AUTHORIZED_TO_CREATE_MEETING,"Toplantı oluşturmaya yetkiniz yok!");
        }
    }

    

    private void validateMeetingRequestRules(LocalDateTime dateTime, Integer attendancePoints){
        if(dateTime != null){
            if (dateTime.isBefore(LocalDateTime.now())){
                throw new BadRequestException(ErrorCode.MEETING_DATE_INVALID,"Bu tarihte toplantı oluşturulamaz!");
            }
            if (dateTime.isBefore(LocalDateTime.now().plusMinutes(30))) {
                throw new BadRequestException(ErrorCode.MEETING_DATE_TOO_SOON,"Toplantı tarihi şu andan en az 30 dakika sonra olmalıdır!");
            }
        }
        if(attendancePoints != null){
            if (attendancePoints < 0){
                throw new BadRequestException(ErrorCode.INVALID_ATTENDANCE_POINT,"Puan negatif olamaz!");
            }
            if (attendancePoints > 1000){
                throw new BadRequestException(ErrorCode.ATTENDANCE_POINT_OUT_OF_RANGE,"Puan çok yüksek!");
            }
        }
    }

    private void checkMeetingDuplicate(CreateMeetingRequest request, Long communityId){
        if (meetingRepository.existsByTitleAndDateTimeAndLocationAndCommunityId(request.getTitle(),request.getDateTime(),
                request.getLocation().trim(),communityId)){
            throw new ConflictException(ErrorCode.MEETING_ALREADY_EXISTS,"Bu toplantı kaydı zaten var!");
        }
    }


    private void validateMeetingCommitteeRelation(CreateMeetingRequest request){


        if (Boolean.TRUE.equals(request.getIsGeneral()) && request.getCommitteeId() != null ){
            throw new BadRequestException(ErrorCode.INVALID_MEETING_COMMITTEE_RELATION,"Genel toplantıda komite girilmez!");
        }

        if (Boolean.FALSE.equals(request.getIsGeneral()) && request.getCommitteeId() == null){
            throw new BadRequestException(ErrorCode.INVALID_MEETING_COMMITTEE_RELATION,"Lütfen komiteyi belirtiniz!");
        }
    }

    private Committee resolveCommittee(Long committeeId, User user) {
        if (committeeId == null) {
            return null;
        }

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return committeeRepository.findById(committeeId)
                    .orElseThrow(() -> new NotFoundException(
                            ErrorCode.COMMITTEE_NOT_FOUND,
                            "Komite bulunamadı!"
                    ));
        }

        return committeeRepository.findByIdAndCommunityId(committeeId, user.getCommunity().getId())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.COMMITTEE_NOT_FOUND,
                        "Komite bulunamadı!"
                ));
    }

    private Community resolveMeetingCommunity(User user, Committee committee) {
        return committee != null ? committee.getCommunity() : user.getCommunity();
    }




    @Override
    public MeetingResponse createMeeting(CreateMeetingRequest request) {

        User user = authService.getAuthenticatedUser();

        checkMeetingAuthority();
        validateMeetingRequestRules(request.getDateTime(), request.getAttendancePoints());
        validateMeetingCommitteeRelation(request);
        Committee committee = resolveCommittee(request.getCommitteeId(), user);
        Community targetCommunity = resolveMeetingCommunity(user, committee);
        checkMeetingDuplicate(request, targetCommunity.getId());



        Meeting entity = meetingMapper.toEntity(request);
        entity.setCommunity(targetCommunity);
        entity.setCommittee(committee);
        entity.setQrCodeData(UUID.randomUUID().toString());
        entity.setQrExpiresAt(request.getDateTime().plusHours(1));

        Meeting savedMeeting = meetingRepository.save(entity);
        return meetingMapper.toResponse(savedMeeting);
    }

    private Meeting getMeetingIfExists(Long id){

        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return meetingRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, "Toplantı kaydı bulunamadı!"));
        }

        return meetingRepository.findByIdAndCommunityId(id, user.getCommunity().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, "Toplantı kaydı bulunamadı!"));
    }

    private void validateMeetingQrCode(MeetingAttendanceScanRequest request, Meeting meeting){

        if (!Objects.equals(request.getQrCodeData(), meeting.getQrCodeData())) {

            throw new BadRequestException(ErrorCode.INVALID_MEETING_QR_CODE,"QR kod uyuşmuyor!");

        }
    }

    private void validateMeetingQrNotExpired(Meeting meeting){

        if (meeting.getQrExpiresAt().isBefore(LocalDateTime.now())){
            throw new BadRequestException(ErrorCode.MEETING_QR_CODE_EXPIRED,"QR kodun süresi dolmuş!");
        }
    }

    private void checkUserAlreadyAttendedMeetingId(Long meetId, Long userId, Long communityId){
        if (meetingAttendanceRepository.existsByMeetingIdAndUserIdAndCommunityId(meetId, userId, communityId)){
            throw new ConflictException(ErrorCode.MEETING_ATTENDANCE_ALREADY_EXISTS,"Bu toplantı için katılım kaydınız zaten mevcut!");
        }
    }

    private MeetingAttendance buildMeetingAttendance(Meeting meeting, User user){

        MeetingAttendance meetingAttendance = new MeetingAttendance();
        meetingAttendance.setMeeting(meeting);
        meetingAttendance.setScannedAt(LocalDateTime.now());
        meetingAttendance.setUser(user);
        meetingAttendance.setPointsEarned(meeting.getAttendancePoints());
        meetingAttendance.setCommunity(meeting.getCommunity());

        Integer meetingAttendancePoints = meeting.getAttendancePoints();
        Integer totalPoints = user.getTotalPoints();

        user.setTotalPoints(meetingAttendancePoints + totalPoints);
        return meetingAttendance;


    }

    @Transactional
    @Override
    public MeetingAttendanceResponse scanMeetingAttendance(MeetingAttendanceScanRequest request) {

        User authenticatedUser = authService.getAuthenticatedUser();
        Meeting meeting = getMeetingIfExists(request.getMeetingId());

        validateMeetingQrCode(request,meeting);
        validateMeetingQrNotExpired(meeting);
        checkUserAlreadyAttendedMeetingId(request.getMeetingId(), authenticatedUser.getId(),authenticatedUser.getCommunity().getId());

        MeetingAttendance meetingAttendance = buildMeetingAttendance(meeting, authenticatedUser);
        MeetingAttendance savedMeetingAttendance = meetingAttendanceRepository.save(meetingAttendance);

        userRepository.save(authenticatedUser);

        return meetingAttendanceMapper.toResponse(savedMeetingAttendance);
    }


    @Override
    public MeetingResponse getMeetingById(Long meetingId) {
        Meeting meeting = getMeetingIfExists(meetingId);
        return meetingMapper.toResponse(meeting);
    }

    @Transactional
    @Override
    public void deleteMeeting(Long meetingId){
        checkMeetingAuthority();
        Meeting meeting = getMeetingIfExists(meetingId);

        List<MeetingAttendance> attendances = meetingAttendanceRepository.findAllByMeetingIdAndCommunityId(meetingId, meeting.getCommunity().getId());
        for (MeetingAttendance attendance : attendances) {
            User user = attendance.getUser();
            if (user != null) {
                int currentPoints = user.getTotalPoints() != null ? user.getTotalPoints() : 0;
                int pointsEarned = attendance.getPointsEarned() != null ? attendance.getPointsEarned() : 0;
                user.setTotalPoints(Math.max(0, currentPoints - pointsEarned));
            }
        }

        meetingAttendanceRepository.deleteAll(attendances);
        meetingRepository.delete(meeting);

    }

    @Override
    public List<MeetingResponse> getAllMeetings(){
        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return meetingMapper.toResponseList(meetingRepository.findAll());
        }

        List<Meeting> meetings = meetingRepository.findByCommunityId(user.getCommunity().getId());
        return meetingMapper.toResponseList(meetings);
    }

    @Override
    public List<AttendanceListItemResponse> getMeetingAttendances(Long meetingId) {
        Meeting meeting = getMeetingIfExists(meetingId);
        return meetingAttendanceRepository.findAllByMeetingIdAndCommunityId(meetingId, meeting.getCommunity().getId())
                .stream()
                .map(this::toAttendanceListItem)
                .toList();
    }

    @Override
    public MeetingResponse updateMeeting(Long meetingId, UpdateMeetingRequest request) {

        checkMeetingAuthority();

        Meeting meeting = getMeetingIfExists(meetingId);

        validateMeetingRequestRules(request.getDateTime(),request.getAttendancePoints());

        validateMeetingCommitteeRelationUpdate(meeting, request);

        checkMeetingDuplicateForUpdate(meeting, request);

        updateMeetingFields(meeting, request);

        Committee committee = resolveCommittee(request.getCommitteeId(), authService.getAuthenticatedUser());

        if (Boolean.TRUE.equals(request.getIsGeneral())) {
            meeting.setCommittee(null);
        } else if(committee != null){
            meeting.setCommittee(committee);
            meeting.setCommunity(committee.getCommunity());
        }

        Meeting updatedMeeting = meetingRepository.save(meeting);
        return meetingMapper.toResponse(updatedMeeting);

        


    }

    private void validateMeetingCommitteeRelationUpdate(Meeting meeting , UpdateMeetingRequest request){

        Boolean finalIsGeneral;

        if (request.getIsGeneral() != null){
            finalIsGeneral = request.getIsGeneral();
        } else {
            finalIsGeneral = meeting.getIsGeneral();
        }

        Long finalCommitteeId;

        if(Boolean.TRUE.equals(finalIsGeneral)){
            finalCommitteeId = null;
        } else {
            if (request.getCommitteeId() != null){
                finalCommitteeId = request.getCommitteeId();
            } else {
                finalCommitteeId = meeting.getCommittee() != null ? meeting.getCommittee().getId() : null;
            }
        }

        if(Boolean.TRUE.equals(finalIsGeneral)&& request.getCommitteeId()!=null){
            throw new BadRequestException(ErrorCode.INVALID_MEETING_COMMITTEE_RELATION,"Genel toplantıda komite girilmez!");
        }

        if (Boolean.FALSE.equals(finalIsGeneral) && finalCommitteeId == null){
            throw new BadRequestException(ErrorCode.INVALID_MEETING_COMMITTEE_RELATION,"Lütfen komiteyi belirtiniz!");
        }
    }

    private void checkMeetingDuplicateForUpdate(Meeting meeting, UpdateMeetingRequest updateMeetingRequest){

        String title;
        if(updateMeetingRequest.getTitle() != null){
            title = updateMeetingRequest.getTitle().trim();
        }
        else {
            title = meeting.getTitle();
        }

        LocalDateTime dateTime;
        if(updateMeetingRequest.getDateTime() != null){
            dateTime = updateMeetingRequest.getDateTime();
        }
        else {
            dateTime = meeting.getDateTime();
        }

        String location;
        if(updateMeetingRequest.getLocation() != null){
            location = updateMeetingRequest.getLocation().trim();
        }
        else {
            location = meeting.getLocation();
        }

        
        if (meetingRepository.existsByTitleAndDateTimeAndLocationAndCommunityIdAndIdNot(title,dateTime,location,meeting.getCommunity().getId(),meeting.getId())){
            throw new ConflictException(ErrorCode.MEETING_ALREADY_EXISTS,"Bu toplantı kaydı zaten var!");
        }
    }

    private void updateMeetingFields(Meeting meeting, UpdateMeetingRequest updateMeetingRequest){

        if(updateMeetingRequest.getTitle() != null){
            meeting.setTitle(updateMeetingRequest.getTitle().trim());
        }

        if(updateMeetingRequest.getDescription() != null){
            meeting.setDescription(updateMeetingRequest.getDescription().trim());
        }

        if(updateMeetingRequest.getLocation() !=null){
            meeting.setLocation(updateMeetingRequest.getLocation().trim());
        }

        if(updateMeetingRequest.getDateTime() != null){
            meeting.setDateTime(updateMeetingRequest.getDateTime());
            meeting.setQrExpiresAt(updateMeetingRequest.getDateTime().plusHours(1));
        }
        if(updateMeetingRequest.getAttendancePoints() != null){
            meeting.setAttendancePoints(updateMeetingRequest.getAttendancePoints());
        }

        if (updateMeetingRequest.getPosterImage() != null) {
            meeting.setPosterImage(updateMeetingRequest.getPosterImage().trim());
        }

        if (updateMeetingRequest.getApprovalStatus() != null) {
            meeting.setApprovalStatus(updateMeetingRequest.getApprovalStatus());
        }

        if (updateMeetingRequest.getEventType() != null) {
            meeting.setEventType(updateMeetingRequest.getEventType());
        }

        if (updateMeetingRequest.getMeetingType() != null) {
            meeting.setMeetingType(updateMeetingRequest.getMeetingType());
        }

        if (updateMeetingRequest.getAgendaItems() != null) {
            meeting.setAgendaItems(updateMeetingRequest.getAgendaItems().trim());
        }

        if (updateMeetingRequest.getNotes() != null) {
            meeting.setNotes(updateMeetingRequest.getNotes().trim());
        }

        if (updateMeetingRequest.getDecisions() != null) {
            meeting.setDecisions(updateMeetingRequest.getDecisions().trim());
        }

        if (updateMeetingRequest.getActions() != null) {
            meeting.setActions(updateMeetingRequest.getActions().trim());
        }


        if(updateMeetingRequest.getIsGeneral() != null){
            if(Boolean.TRUE.equals(updateMeetingRequest.getIsGeneral())){
                meeting.setIsGeneral(true);
                meeting.setCommittee(null);
            }
            else{
                meeting.setIsGeneral(false);
            }
        }



    }

    private AttendanceListItemResponse toAttendanceListItem(MeetingAttendance attendance) {
        User attendanceUser = attendance.getUser();
        String firstName = attendanceUser.getFirstName() != null ? attendanceUser.getFirstName() : "";
        String lastName = attendanceUser.getLastName() != null ? attendanceUser.getLastName() : "";
        String fullName = (firstName + " " + lastName).trim();

        return new AttendanceListItemResponse(
                attendance.getId(),
                attendance.getScannedAt(),
                attendance.getPointsEarned(),
                attendanceUser.getId(),
                attendanceUser.getUsername(),
                fullName.isBlank() ? attendanceUser.getUsername() : fullName,
                attendanceUser.getProfileImage()
        );
    }


}
